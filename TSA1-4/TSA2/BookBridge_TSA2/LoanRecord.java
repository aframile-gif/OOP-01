public class LoanRecord {
    private static int totalLoansProcessed = 0; // Class-level static member
    
    private Member borrower;
    private LibraryBook book;
    private int requestedDays;
    private int lateDays;
    private String status;
    private double estimatedFine;

    public LoanRecord(Member borrower, LibraryBook book, int requestedDays, int lateDays) {
        if (requestedDays < 1 || requestedDays > 30 || lateDays < 0) {
            throw new IllegalArgumentException("Invalid day values.");
        }
        this.borrower = borrower;
        this.book = book;
        this.requestedDays = requestedDays;
        this.lateDays = lateDays;
        totalLoansProcessed++;
        
        processLoan();
    }

    private void processLoan() {
        if (!book.isAvailable()) {
            this.status = "RESERVED";
            this.estimatedFine = 0;
        } else if (requestedDays <= borrower.getMaximumLoanDays()) {
            this.status = "APPROVED";
            // Polymorphism: Calculates fine dynamically based on actual member type
            this.estimatedFine = lateDays * borrower.getDailyFine(); 
        } else {
            this.status = "DENIED";
            this.estimatedFine = 0;
        }
    }

    public String getStatus() { return status; }
    public double getEstimatedFine() { return estimatedFine; }
    public int getRequestedDays() { return requestedDays; }
    public static int getTotalLoans() { return totalLoansProcessed; }
}