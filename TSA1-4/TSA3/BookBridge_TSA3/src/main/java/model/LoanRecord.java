package model;

import java.time.LocalDate;

public class LoanRecord {
    public enum LoanStatus { APPROVED, RESERVED, DENIED, RETURNED, OVERDUE }

    private static int totalLoansProcessed = 0;
    private String loanId;
    private Member borrower;
    private LibraryBook book;
    private int requestedDays;
    private int lateDays;
    private LocalDate dueDate;
    private LoanStatus status;
    private double estimatedFine;

    public LoanRecord(Member borrower, LibraryBook book, int requestedDays, int lateDays) {
        if (requestedDays < 1 || requestedDays > 30 || lateDays < 0) {
            throw new IllegalArgumentException("Invalid day values.");
        }
        this.borrower = borrower;
        this.book = book;
        this.requestedDays = requestedDays;
        this.lateDays = lateDays;
        this.loanId = "LN-" + (++totalLoansProcessed);
        this.dueDate = LocalDate.now().plusDays(requestedDays).minusDays(lateDays);
        this.status = (lateDays > 0) ? LoanStatus.OVERDUE : LoanStatus.APPROVED;
        this.estimatedFine = lateDays * borrower.getDailyFine();
    }

    public String getLoanId() { return loanId; }
    public Member getBorrower() { return borrower; }
    public LibraryBook getBook() { return book; }
    public LoanStatus getStatus() { return status; }
    public double getEstimatedFine() { return estimatedFine; }
    public int getRequestedDays() { return requestedDays; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isOverdue() { return lateDays > 0 || status == LoanStatus.OVERDUE; }
    public void markReturned() { this.status = LoanStatus.RETURNED; }
}