package dao;

import model.*;
import model.LibraryBook.BookStatus;
import model.LoanRecord.LoanStatus;
import model.Member.MembershipType;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class BookBridgeDao {

    // --- MEMBERS ---
    public void insertMember(Member m) throws SQLException {
        String sql = "INSERT INTO members (member_id, name, membership_type) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getMemberId());
            ps.setString(2, m.getName());
            ps.setString(3, m.getType().name());
            if (ps.executeUpdate() != 1) throw new SQLException("Failed to insert member.");
        }
    }

    public Optional<Member> findMemberById(String memberId) throws SQLException {
        String sql = "SELECT member_id, name, membership_type FROM members WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Member.create(
                            rs.getString("member_id"),
                            rs.getString("name"),
                            MembershipType.valueOf(rs.getString("membership_type"))));
                }
            }
        }
        return Optional.empty();
    }

    public List<Member> findAllMembers() throws SQLException {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT member_id, name, membership_type FROM members ORDER BY member_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(Member.create(
                        rs.getString("member_id"),
                        rs.getString("name"),
                        MembershipType.valueOf(rs.getString("membership_type"))));
            }
        }
        return list;
    }

    // --- BOOKS ---
    public void insertBook(LibraryBook b) throws SQLException {
        String sql = "INSERT INTO books (isbn, title, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getIsbn());
            ps.setString(2, b.getTitle());
            ps.setString(3, b.getStatus().name());
            if (ps.executeUpdate() != 1) throw new SQLException("Failed to insert book.");
        }
    }

    public Optional<LibraryBook> findBookByIsbn(String isbn) throws SQLException {
        String sql = "SELECT isbn, title, status FROM books WHERE isbn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LibraryBook b = new LibraryBook(rs.getString("isbn"), rs.getString("title"), true);
                    b.setStatus(BookStatus.valueOf(rs.getString("status")));
                    return Optional.of(b);
                }
            }
        }
        return Optional.empty();
    }

    public List<LibraryBook> searchBooks(String keyword) throws SQLException {
        List<LibraryBook> list = new ArrayList<>();
        String sql = "SELECT isbn, title, status FROM books WHERE title LIKE ? OR isbn LIKE ? ORDER BY isbn";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LibraryBook b = new LibraryBook(rs.getString("isbn"), rs.getString("title"), true);
                    b.setStatus(BookStatus.valueOf(rs.getString("status")));
                    list.add(b);
                }
            }
        }
        return list;
    }

    public boolean updateBookStatus(String isbn, BookStatus status) throws SQLException {
        String sql = "UPDATE books SET status = ? WHERE isbn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, isbn);
            return ps.executeUpdate() == 1;
        }
    }

    // --- LOANS ---
    public int countActiveLoansForMember(String memberId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM loans WHERE member_id = ? AND status IN ('APPROVED', 'OVERDUE')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public void insertLoan(LoanRecord loan) throws SQLException {
        String sql = "INSERT INTO loans (loan_id, member_id, isbn, requested_days, late_days, due_date, status, estimated_fine) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loan.getLoanId());
            ps.setString(2, loan.getBorrower().getMemberId());
            ps.setString(3, loan.getBook().getIsbn());
            ps.setInt(4, loan.getRequestedDays());
            ps.setInt(5, (int) Math.round(loan.getEstimatedFine() / loan.getBorrower().getDailyFine()));
            ps.setDate(6, java.sql.Date.valueOf(loan.getDueDate()));
            ps.setString(7, loan.getStatus().name());
            ps.setDouble(8, loan.getEstimatedFine());
            if (ps.executeUpdate() != 1) throw new SQLException("Failed to insert loan.");
        }
    }

    public Optional<String> returnLoan(String loanId) throws SQLException {
        String findSql = "SELECT isbn FROM loans WHERE loan_id = ? AND status IN ('APPROVED', 'OVERDUE')";
        String updateSql = "UPDATE loans SET status = 'RETURNED' WHERE loan_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement findPs = conn.prepareStatement(findSql)) {
            findPs.setString(1, loanId);
            try (ResultSet rs = findPs.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                String isbn = rs.getString("isbn");
                try (PreparedStatement updPs = conn.prepareStatement(updateSql)) {
                    updPs.setString(1, loanId);
                    if (updPs.executeUpdate() == 1) return Optional.of(isbn);
                }
            }
        }
        return Optional.empty();
    }

    public List<LoanRecord> findAllLoans() throws SQLException {
        List<LoanRecord> list = new ArrayList<>();
        String sql = "SELECT l.*, m.name, m.membership_type, b.title, b.status AS book_status "
                   + "FROM loans l "
                   + "JOIN members m ON l.member_id = m.member_id "
                   + "JOIN books b ON l.isbn = b.isbn "
                   + "ORDER BY (l.status = 'OVERDUE') DESC, l.due_date ASC, l.loan_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Member m = Member.create(rs.getString("member_id"), rs.getString("name"),
                        MembershipType.valueOf(rs.getString("membership_type")));
                LibraryBook b = new LibraryBook(rs.getString("isbn"), rs.getString("title"), true);
                b.setStatus(BookStatus.valueOf(rs.getString("book_status")));
                list.add(new LoanRecord(
                        rs.getString("loan_id"), m, b,
                        rs.getInt("requested_days"), rs.getInt("late_days"),
                        rs.getDate("due_date").toLocalDate(),
                        LoanStatus.valueOf(rs.getString("status")),
                        rs.getDouble("estimated_fine")));
            }
        }
        return list;
    }

    // --- RESERVATIONS ---
    public void insertReservation(Reservation res) throws SQLException {
        String sql = "INSERT INTO reservations (reservation_id, member_id, isbn, status) VALUES (?, ?, ?, 'PENDING')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, res.getReservationId());
            ps.setString(2, res.getMember().getMemberId());
            ps.setString(3, res.getBook().getIsbn());
            if (ps.executeUpdate() != 1) throw new SQLException("Failed to insert reservation.");
        }
    }

    public boolean cancelReservation(String reservationId) throws SQLException {
        String sql = "UPDATE reservations SET status = 'CANCELLED' WHERE reservation_id = ? AND status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationId);
            return ps.executeUpdate() == 1;
        }
    }

    public Optional<Reservation> pollNextPendingReservation() throws SQLException {
        String sql = "SELECT r.reservation_id, r.status, m.member_id, m.name, m.membership_type, b.isbn, b.title "
                   + "FROM reservations r "
                   + "JOIN members m ON r.member_id = m.member_id "
                   + "JOIN books b ON r.isbn = b.isbn "
                   + "WHERE r.status = 'PENDING' ORDER BY r.created_at ASC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String resId = rs.getString("reservation_id");
                Member m = Member.create(rs.getString("member_id"), rs.getString("name"),
                        MembershipType.valueOf(rs.getString("membership_type")));
                LibraryBook b = new LibraryBook(rs.getString("isbn"), rs.getString("title"), false);

                try (PreparedStatement upd = conn.prepareStatement(
                        "UPDATE reservations SET status = 'FULFILLED' WHERE reservation_id = ?")) {
                    upd.setString(1, resId);
                    upd.executeUpdate();
                }
                return Optional.of(new Reservation(resId, m, b, "FULFILLED"));
            }
        }
        return Optional.empty();
    }

    public List<Reservation> findAllReservations() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.reservation_id, r.status, m.member_id, m.name, m.membership_type, b.isbn, b.title "
                   + "FROM reservations r "
                   + "JOIN members m ON r.member_id = m.member_id "
                   + "JOIN books b ON r.isbn = b.isbn "
                   + "ORDER BY r.created_at ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Member m = Member.create(rs.getString("member_id"), rs.getString("name"),
                        MembershipType.valueOf(rs.getString("membership_type")));
                LibraryBook b = new LibraryBook(rs.getString("isbn"), rs.getString("title"), false);
                list.add(new Reservation(rs.getString("reservation_id"), m, b, rs.getString("status")));
            }
        }
        return list;
    }
}