package dao;

import model.ServiceRequest;
import model.ServiceRequest.RequestCategory;
import model.ServiceRequest.TicketStatus;
import model.Student;

import java.sql.*;
import java.util.*;

public class CampusConnectDao {

    // --- STUDENTS ---
    public void insertStudent(Student s) throws SQLException {
        String sql = "INSERT INTO students (student_id, name) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getId());
            ps.setString(2, s.getName());
            if (ps.executeUpdate() != 1) throw new SQLException("Failed to insert student.");
        }
    }

    public Optional<Student> findStudentById(String studentId) throws SQLException {
        String sql = "SELECT student_id, name FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Student(rs.getString("student_id"), rs.getString("name")));
                }
            }
        }
        return Optional.empty();
    }

    public List<Student> findAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT student_id, name FROM students ORDER BY student_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Student(rs.getString("student_id"), rs.getString("name")));
            }
        }
        return list;
    }

    // --- SERVICE REQUESTS ---
    public void insertRequest(ServiceRequest req) throws SQLException {
        String sql = "INSERT INTO service_requests (ticket_id, student_id, category, description, priority, estimated_days, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, req.getTicketId());
            ps.setString(2, req.getStudent().getId());
            ps.setString(3, req.getCategory().name());
            ps.setString(4, req.getDescription());
            ps.setInt(5, req.getPriority());
            ps.setInt(6, req.estimateDays());
            ps.setString(7, req.getStatus().name());
            if (ps.executeUpdate() != 1) throw new SQLException("Failed to insert ticket.");
        }
    }

    public boolean existsActiveDuplicate(String studentId, RequestCategory cat, String desc) throws SQLException {
        String sql = "SELECT COUNT(*) FROM service_requests "
                   + "WHERE student_id = ? AND category = ? AND LOWER(description) = LOWER(?) AND status != 'RESOLVED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, cat.name());
            ps.setString(3, desc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public List<ServiceRequest> searchAndFilter(String keyword, String categoryFilter) throws SQLException {
        List<ServiceRequest> list = new ArrayList<>();
        boolean filterCat = categoryFilter != null && !categoryFilter.equals("ALL");

        String sql = "SELECT r.*, s.name FROM service_requests r "
                   + "JOIN students s ON r.student_id = s.student_id "
                   + "WHERE (r.description LIKE ? OR r.ticket_id LIKE ? OR s.name LIKE ?) "
                   + (filterCat ? "AND r.category = ? " : "")
                   + "ORDER BY r.priority DESC, r.estimated_days ASC, r.ticket_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            if (filterCat) ps.setString(4, categoryFilter);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student(rs.getString("student_id"), rs.getString("name"));
                    list.add(ServiceRequest.createFromDb(
                            rs.getString("ticket_id"), s,
                            RequestCategory.valueOf(rs.getString("category")),
                            rs.getString("description"),
                            rs.getInt("priority"),
                            TicketStatus.valueOf(rs.getString("status"))));
                }
            }
        }
        return list;
    }

    public boolean updateStatus(String ticketId, TicketStatus newStatus) throws SQLException {
        String sql = "UPDATE service_requests SET status = ? WHERE ticket_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setString(2, ticketId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteRequest(String ticketId) throws SQLException {
        String sql = "DELETE FROM service_requests WHERE ticket_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketId);
            return ps.executeUpdate() == 1;
        }
    }

    // FIFO Queue:
    public Optional<ServiceRequest> pollNextOpenRequest() throws SQLException {
        String sql = "SELECT r.*, s.name FROM service_requests r "
                   + "JOIN students s ON r.student_id = s.student_id "
                   + "WHERE r.status = 'OPEN' ORDER BY r.created_at ASC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String ticketId = rs.getString("ticket_id");
                Student s = new Student(rs.getString("student_id"), rs.getString("name"));
                ServiceRequest req = ServiceRequest.createFromDb(
                        ticketId, s,
                        RequestCategory.valueOf(rs.getString("category")),
                        rs.getString("description"),
                        rs.getInt("priority"),
                        TicketStatus.RESOLVED);
                updateStatus(ticketId, TicketStatus.RESOLVED);
                return Optional.of(req);
            }
        }
        return Optional.empty();
    }
}