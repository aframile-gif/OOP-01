package service;

import exception.*;
import model.*;
import model.ServiceRequest.RequestCategory;
import model.ServiceRequest.TicketStatus;
import java.util.*;
import java.util.stream.Collectors;

public class CampusService {
    
    public record ServiceSummary(
        long openTickets,
        long resolvedTickets,
        Map<RequestCategory, Long> countByCategory,
        double averageEstimatedDays
    ) {}

    private final Map<String, Student> students = new HashMap<>();
    private final Map<String, ServiceRequest> activeTickets = new HashMap<>();
    private final Queue<ServiceRequest> pendingQueue = new LinkedList<>();
    private final Set<String> registeredStudentIds = new HashSet<>();
    private final List<ServiceRequest> ticketHistory = new ArrayList<>();

    // Generic lookup abstraction
    public <K, V> Optional<V> findByKey(Map<K, V> map, K key) {
        return Optional.ofNullable(map.get(key));
    }

    public void registerStudent(Student student) throws DuplicateTicketException {
        if (registeredStudentIds.contains(student.getId())) {
            throw new DuplicateTicketException("Student ID already registered: " + student.getId());
        }
        students.put(student.getId(), student);
        registeredStudentIds.add(student.getId());
    }

    public ServiceRequest submitRequest(String studentId, RequestCategory cat, String desc, int priority)
            throws InvalidPriorityException, TicketNotFoundException, DuplicateTicketException {
        Student student = findByKey(students, studentId)
                .orElseThrow(() -> new TicketNotFoundException("Student not found: " + studentId));

        if (priority < 1 || priority > 5) {
            throw new InvalidPriorityException("Priority must be between 1 and 5. Given: " + priority);
        }

        boolean duplicateActive = activeTickets.values().stream()
                .anyMatch(t -> t.getStudent().getId().equals(studentId)
                        && t.getCategory() == cat
                        && t.getDescription().equalsIgnoreCase(desc));
        if (duplicateActive) {
            throw new DuplicateTicketException("Duplicate active request for student " + studentId);
        }

        ServiceRequest req = ServiceRequest.create(student, cat, desc, priority);
        activeTickets.put(req.getTicketId(), req);
        pendingQueue.offer(req);
        ticketHistory.add(req);
        return req;
    }

    public Optional<ServiceRequest> processNextRequest() {
        ServiceRequest next = pendingQueue.poll();
        if (next != null) {
            next.setStatus(TicketStatus.RESOLVED);
            activeTickets.remove(next.getTicketId());
        }
        return Optional.ofNullable(next);
    }

    // Defensive view sorted by: Priority descending, Estimated Days ascending, Ticket ID ascending
    public List<ServiceRequest> getSortedRequestsView() {
        return ticketHistory.stream()
                .sorted(Comparator.comparingInt(ServiceRequest::getPriority).reversed()
                        .thenComparingInt(ServiceRequest::estimateDays)
                        .thenComparing(ServiceRequest::getTicketId))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<ServiceRequest> searchByKeyword(String keyword) {
        String k = keyword.toLowerCase().trim();
        return ticketHistory.stream()
                .filter(r -> r.getDescription().toLowerCase().contains(k))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<ServiceRequest> filterByCategory(RequestCategory category) {
        return ticketHistory.stream()
                .filter(r -> r.getCategory() == category)
                .toList();
    }

    public ServiceSummary generateSummary() {
        long open = activeTickets.size();
        long resolved = ticketHistory.stream().filter(r -> r.getStatus() == TicketStatus.RESOLVED).count();
        Map<RequestCategory, Long> byCat = ticketHistory.stream()
                .collect(Collectors.groupingBy(ServiceRequest::getCategory, Collectors.counting()));
        double avgDays = ticketHistory.stream().mapToInt(ServiceRequest::estimateDays).average().orElse(0.0);

        return new ServiceSummary(open, resolved, byCat, avgDays);
    }
}