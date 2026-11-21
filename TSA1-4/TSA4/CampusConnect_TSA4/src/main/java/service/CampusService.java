package service;

import dao.CampusConnectDao;
import exception.*;
import model.ServiceRequest;
import model.ServiceRequest.RequestCategory;
import model.ServiceRequest.TicketStatus;
import model.Student;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CampusService {
    public record ServiceSummary(
        long openTickets,
        long inProgressTickets,
        long resolvedTickets,
        Map<RequestCategory, Long> countByCategory,
        double averageEstimatedDays,
        int processedInSession
    ) {}

    private final CampusConnectDao dao = new CampusConnectDao();
    private final Object queueLock = new Object();
    private final AtomicInteger sessionProcessedCount = new AtomicInteger(0);

    public void registerStudent(String id, String name) throws DuplicateTicketException, SQLException {
        Student s = new Student(id, name);
        if (dao.findStudentById(s.getId()).isPresent()) {
            throw new DuplicateTicketException("Student ID already registered: " + s.getId());
        }
        dao.insertStudent(s);
    }

    public ServiceRequest submitRequest(String studentId, RequestCategory cat, String desc, int priority)
            throws InvalidPriorityException, TicketNotFoundException, DuplicateTicketException, SQLException {
        if (studentId == null || studentId.isBlank() || desc == null || desc.isBlank()) {
            throw new IllegalArgumentException("Student ID and Description are required.");
        }
        if (priority < 1 || priority > 5) {
            throw new InvalidPriorityException("Priority must be between 1 and 5. Given: " + priority);
        }

        Student student = dao.findStudentById(studentId.trim())
                .orElseThrow(() -> new TicketNotFoundException("Student ID not found: " + studentId.trim()));

        if (dao.existsActiveDuplicate(student.getId(), cat, desc.trim())) {
            throw new DuplicateTicketException("Duplicate active request already exists for student " + student.getId());
        }

        String uniqueTicketId = "TKT-" + (System.currentTimeMillis() % 100000);
        ServiceRequest req = ServiceRequest.createFromDb(
                uniqueTicketId, student, cat, desc.trim(), priority, TicketStatus.OPEN);
        dao.insertRequest(req);
        return req;
    }

    public void updateTicketStatus(String ticketId, TicketStatus newStatus)
            throws TicketNotFoundException, SQLException {
        if (!dao.updateStatus(ticketId.trim(), newStatus)) {
            throw new TicketNotFoundException("Ticket not found: " + ticketId);
        }
    }

    public void deleteTicket(String ticketId) throws TicketNotFoundException, SQLException {
        if (!dao.deleteRequest(ticketId.trim())) {
            throw new TicketNotFoundException("Ticket not found: " + ticketId);
        }
    }

    public Optional<ServiceRequest> processNextRequest() throws SQLException {
        synchronized (queueLock) {
            Optional<ServiceRequest> next = dao.pollNextOpenRequest();
            if (next.isPresent()) {
                sessionProcessedCount.incrementAndGet();
            }
            return next;
        }
    }

    public List<Student> listStudents() throws SQLException {
        return dao.findAllStudents();
    }

    public List<ServiceRequest> searchTickets(String keyword, String categoryFilter) throws SQLException {
        return dao.searchAndFilter(keyword == null ? "" : keyword.trim(), categoryFilter);
    }

    public ServiceSummary generateSummary() throws SQLException {
        List<ServiceRequest> all = dao.searchAndFilter("", "ALL");
        long open = all.stream().filter(r -> r.getStatus() == TicketStatus.OPEN).count();
        long inProg = all.stream().filter(r -> r.getStatus() == TicketStatus.IN_PROGRESS).count();
        long resolved = all.stream().filter(r -> r.getStatus() == TicketStatus.RESOLVED).count();

        Map<RequestCategory, Long> byCat = all.stream()
                .collect(Collectors.groupingBy(ServiceRequest::getCategory, Collectors.counting()));
        double avgDays = all.stream().mapToInt(ServiceRequest::estimateDays).average().orElse(0.0);

        return new ServiceSummary(open, inProg, resolved, byCat, avgDays, sessionProcessedCount.get());
    }
}