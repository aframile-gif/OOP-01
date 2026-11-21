package service;

import dao.BookBridgeDao;
import exception.*;
import model.*;
import model.LibraryBook.BookStatus;
import model.Member.MembershipType;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LibraryService {
    public record CirculationSummary(
        long availableBooks,
        long checkedOutBooks,
        Map<MembershipType, Long> activeLoansByType,
        double totalEstimatedFines,
        int processedReservationsSessionCount
    ) {}

    private final BookBridgeDao dao;
    
    private final Object reservationLock = new Object();
    private final AtomicInteger processedReservationsCount = new AtomicInteger(0);

    public LibraryService() {
        this.dao = new BookBridgeDao();
    }

    public void registerMember(String id, String name, MembershipType type)
            throws DuplicateEntityException, SQLException {
        Member m = Member.create(id, name, type);
        if (dao.findMemberById(m.getMemberId()).isPresent()) {
            throw new DuplicateEntityException("Member ID already exists: " + m.getMemberId());
        }
        dao.insertMember(m);
    }

    public void addBook(String isbn, String title) throws DuplicateEntityException, SQLException {
        LibraryBook b = new LibraryBook(isbn, title, true);
        if (dao.findBookByIsbn(b.getIsbn()).isPresent()) {
            throw new DuplicateEntityException("Book ISBN already exists: " + b.getIsbn());
        }
        dao.insertBook(b);
    }

    public LoanRecord issueLoan(String memberId, String isbn, int requestedDays, int lateDays)
            throws ItemUnavailableException, LoanLimitExceededException, SQLException {
        Member member = dao.findMemberById(memberId.trim())
                .orElseThrow(() -> new NoSuchElementException("Member not found: " + memberId));
        LibraryBook book = dao.findBookByIsbn(isbn.trim())
                .orElseThrow(() -> new NoSuchElementException("Book not found: " + isbn));

        if (!book.isAvailable()) {
            throw new ItemUnavailableException("Book " + isbn + " is currently " + book.getStatus() + ".");
        }
        if (requestedDays > member.getMaximumLoanDays()) {
            throw new LoanLimitExceededException(member.getType() + " max loan days is " + member.getMaximumLoanDays());
        }

        int maxActive = switch (member.getType()) {
            case STUDENT -> 3;
            case FACULTY -> 5;
            case GUEST -> 1;
        };
        if (dao.countActiveLoansForMember(member.getMemberId()) >= maxActive) {
            throw new LoanLimitExceededException(member.getType() + " active loan limit (" + maxActive + ") reached.");
        }

        String uniqueLoanId = "LN-" + (System.currentTimeMillis() % 100000);
        LoanRecord loan = new LoanRecord(
                uniqueLoanId, member, book, requestedDays, lateDays,
                java.time.LocalDate.now().plusDays(requestedDays).minusDays(lateDays),
                lateDays > 0 ? LoanRecord.LoanStatus.OVERDUE : LoanRecord.LoanStatus.APPROVED,
                lateDays * member.getDailyFine());

        dao.insertLoan(loan);
        dao.updateBookStatus(book.getIsbn(), BookStatus.CHECKED_OUT);
        return loan;
    }

    public void returnBook(String loanId) throws SQLException {
        Optional<String> returnedIsbn = dao.returnLoan(loanId.trim());
        if (returnedIsbn.isEmpty()) {
            throw new NoSuchElementException("Active loan not found or already returned: " + loanId);
        }
        dao.updateBookStatus(returnedIsbn.get(), BookStatus.AVAILABLE);
    }

    public Reservation reserveBook(String memberId, String isbn) throws DuplicateEntityException, SQLException {
        synchronized (reservationLock) {
            Member member = dao.findMemberById(memberId.trim())
                    .orElseThrow(() -> new NoSuchElementException("Member not found: " + memberId));
            LibraryBook book = dao.findBookByIsbn(isbn.trim())
                    .orElseThrow(() -> new NoSuchElementException("Book not found: " + isbn));

            if (book.isAvailable()) {
                throw new IllegalStateException("Book is available; issue a loan instead of reserving.");
            }
            boolean exists = dao.findAllReservations().stream()
                    .anyMatch(r -> r.getStatus().equals("PENDING")
                            && r.getMember().getMemberId().equals(member.getMemberId())
                            && r.getBook().getIsbn().equals(book.getIsbn()));
            if (exists) {
                throw new DuplicateEntityException("Pending reservation already exists for this member and ISBN.");
            }

            String resId = "RES-" + (System.currentTimeMillis() % 100000);
            Reservation res = new Reservation(resId, member, book, "PENDING");
            dao.insertReservation(res);
            dao.updateBookStatus(book.getIsbn(), BookStatus.RESERVED);
            return res;
        }
    }

    public void cancelReservation(String reservationId) throws SQLException {
        synchronized (reservationLock) {
            if (!dao.cancelReservation(reservationId.trim())) {
                throw new NoSuchElementException("Pending reservation not found: " + reservationId);
            }
        }
    }

    public Optional<Reservation> processNextReservation() throws SQLException {
        synchronized (reservationLock) {
            Optional<Reservation> next = dao.pollNextPendingReservation();
            if (next.isPresent()) {
                processedReservationsCount.incrementAndGet();
                next.get().getMember().sendNotification("Reserved book ready: " + next.get().getBook().getTitle());
            }
            return next;
        }
    }

    public List<LibraryBook> searchBooks(String keyword) throws SQLException {
        return dao.searchBooks(keyword == null ? "" : keyword.trim());
    }

    public List<Member> listMembers() throws SQLException { return dao.findAllMembers(); }
    public List<LoanRecord> listLoans() throws SQLException { return dao.findAllLoans(); }
    public List<Reservation> listReservations() throws SQLException { return dao.findAllReservations(); }

    public CirculationSummary generateSummary() throws SQLException {
        List<LibraryBook> books = dao.searchBooks("");
        List<LoanRecord> loans = dao.findAllLoans();

        long avail = books.stream().filter(LibraryBook::isAvailable).count();
        long checkedOut = books.stream().filter(b -> !b.isAvailable()).count();
        Map<MembershipType, Long> byType = loans.stream()
                .filter(l -> l.getStatus() != LoanRecord.LoanStatus.RETURNED)
                .collect(Collectors.groupingBy(l -> l.getBorrower().getType(), Collectors.counting()));
        double fines = loans.stream().mapToDouble(LoanRecord::getEstimatedFine).sum();

        return new CirculationSummary(avail, checkedOut, byType, fines, processedReservationsCount.get());
    }
}