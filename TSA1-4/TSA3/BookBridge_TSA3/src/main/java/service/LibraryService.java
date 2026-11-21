package service;

import exception.*;
import model.*;
import model.LibraryBook.BookStatus;
import model.Member.MembershipType;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryService {
    // Modern Java record right inside LibraryService (no extra file needed!)
    public record CirculationSummary(
        long availableBooks,
        long checkedOutBooks,
        Map<MembershipType, Long> activeLoansByType,
        double totalEstimatedFines
    ) {}

    private final Map<String, LibraryBook> catalog = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();
    private final Map<String, LoanRecord> activeLoans = new HashMap<>();
    private final Queue<Reservation> reservationQueue = new LinkedList<>();
    private final Set<String> checkedOutIsbns = new HashSet<>();
    private final List<LoanRecord> loanHistory = new ArrayList<>();

    // Generic lookup abstraction (Satisfies Requirement 2!)
    public <K, V> Optional<V> findByKey(Map<K, V> map, K key) {
        return Optional.ofNullable(map.get(key));
    }

    public void registerMember(Member member) throws DuplicateEntityException {
        if (members.containsKey(member.getMemberId())) {
            throw new DuplicateEntityException("Member ID already exists: " + member.getMemberId());
        }
        members.put(member.getMemberId(), member);
    }

    public void addBook(LibraryBook book) throws DuplicateEntityException {
        if (catalog.containsKey(book.getIsbn())) {
            throw new DuplicateEntityException("Book ISBN already exists: " + book.getIsbn());
        }
        catalog.put(book.getIsbn(), book);
    }

    public LoanRecord issueLoan(String memberId, String isbn, int requestedDays, int lateDays)
            throws ItemUnavailableException, LoanLimitExceededException {
        Member member = findByKey(members, memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));
        LibraryBook book = findByKey(catalog, isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + isbn));

        if (!book.isAvailable() || checkedOutIsbns.contains(isbn)) {
            throw new ItemUnavailableException("Book " + isbn + " is currently unavailable.");
        }
        if (requestedDays > member.getMaximumLoanDays()) {
            throw new LoanLimitExceededException(member.getType() + " max loan days is " + member.getMaximumLoanDays());
        }

        LoanRecord loan = new LoanRecord(member, book, requestedDays, lateDays);
        book.markCheckedOut();
        checkedOutIsbns.add(isbn);
        activeLoans.put(loan.getLoanId(), loan);
        loanHistory.add(loan);
        return loan;
    }

    public LoanRecord returnBook(String loanId) {
        LoanRecord loan = findByKey(activeLoans, loanId)
                .orElseThrow(() -> new IllegalArgumentException("Active loan not found: " + loanId));
        activeLoans.remove(loanId);
        checkedOutIsbns.remove(loan.getBook().getIsbn());
        loan.markReturned();
        loan.getBook().markReturned();
        return loan;
    }

    public Reservation reserveBook(String memberId, String isbn) throws DuplicateEntityException {
        Member member = findByKey(members, memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));
        LibraryBook book = findByKey(catalog, isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + isbn));

        if (book.isAvailable()) {
            throw new IllegalStateException("Book is available; issue a loan instead.");
        }
        boolean duplicate = reservationQueue.stream()
                .anyMatch(r -> r.getMember().getMemberId().equals(memberId) && r.getBook().getIsbn().equals(isbn));
        if (duplicate) {
            throw new DuplicateEntityException("Already reserved by member " + memberId);
        }

        Reservation res = new Reservation(member, book);
        book.reserve();
        reservationQueue.offer(res);
        return res;
    }

    public Optional<Reservation> processNextReservation() {
        Reservation next = reservationQueue.poll();
        if (next != null) {
            next.getMember().sendNotification("Reserved book ready: " + next.getBook().getTitle());
        }
        return Optional.ofNullable(next);
    }

    public List<LoanRecord> getSortedLoansView() {
        return loanHistory.stream()
                .sorted(Comparator.comparing(LoanRecord::isOverdue).reversed()
                        .thenComparing(LoanRecord::getDueDate)
                        .thenComparing(LoanRecord::getLoanId))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<LibraryBook> searchBooksByTitle(String keyword) {
        return catalog.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(keyword.toLowerCase().trim()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<LibraryBook> filterBooksByStatus(BookStatus status) {
        return catalog.values().stream().filter(b -> b.getStatus() == status).toList();
    }

    public CirculationSummary generateSummary() {
        long avail = catalog.values().stream().filter(LibraryBook::isAvailable).count();
        long checkedOut = checkedOutIsbns.size();
        Map<MembershipType, Long> byType = activeLoans.values().stream()
                .collect(Collectors.groupingBy(l -> l.getBorrower().getType(), Collectors.counting()));
        double fines = loanHistory.stream().mapToDouble(LoanRecord::getEstimatedFine).sum();
        return new CirculationSummary(avail, checkedOut, byType, fines);
    }
}