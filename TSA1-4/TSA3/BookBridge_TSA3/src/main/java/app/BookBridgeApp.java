package app;

import exception.*;
import model.*;
import model.LibraryBook.BookStatus;
import model.Member.MembershipType;
import service.LibraryService;
import java.util.Scanner;

public class BookBridgeApp {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        LibraryService service = new LibraryService();
        int count = 0;

        System.out.println("--- BookBridge TSA3 Collections Library System ---");

        
        while (count < 5) {
            System.out.println("\nTransaction #" + (count + 1));
            try {
                System.out.print("ID: "); String id = in.nextLine().trim();
                System.out.print("Name: "); String name = in.nextLine().trim();
                System.out.print("Type (STUDENT, FACULTY, GUEST): "); String typeStr = in.nextLine().trim().toUpperCase();
                System.out.print("ISBN: "); String isbn = in.nextLine().trim();
                System.out.print("Title: "); String title = in.nextLine().trim();
                System.out.print("Requested Days (1-30): "); int rDays = Integer.parseInt(in.nextLine().trim());
                System.out.print("Available? (true/false): "); boolean avail = Boolean.parseBoolean(in.nextLine().trim());
                System.out.print("Late Days: "); int lDays = Integer.parseInt(in.nextLine().trim());

                MembershipType type = MembershipType.valueOf(typeStr);

                // Register member in Map (if not yet registered)
                try {
                    service.registerMember(Member.create(id, name, type));
                } catch (DuplicateEntityException e) {
                    System.out.println("[Info] Existing member found in Map: " + id);
                }

                // Add book to Catalog Map (if not yet in catalog)
                try {
                    service.addBook(new LibraryBook(isbn, title, avail));
                } catch (DuplicateEntityException e) {
                    System.out.println("[Info] Existing book found in Catalog Map: " + isbn);
                }

                // If unavailable, place in Reservation Queue; otherwise issue Loan
                if (!avail) {
                    Reservation res = service.reserveBook(id, isbn);
                    System.out.printf(">> Result: RESERVED (%s queued) | Est. Fine: PHP 0.00%n", res.getReservationId());
                } else {
                    LoanRecord loan = service.issueLoan(id, isbn, rDays, lDays);
                    System.out.printf(">> Result: %s (%s) | Est. Fine: PHP %.2f%n",
                            loan.getStatus(), loan.getLoanId(), loan.getEstimatedFine());
                }
                count++;

            } catch (ItemUnavailableException | LoanLimitExceededException | DuplicateEntityException e) {
                // Catches your custom TSA3 domain exceptions without crashing!
                System.out.println(">> Result: DENIED (" + e.getMessage() + ") | Est. Fine: PHP 0.00");
                count++;
            } catch (Exception e) {
                System.out.println("Error: Invalid input detected (" + e.getMessage() + "). Please re-enter.");
            }
        }

        // After the 5 transactions, print the TSA3 Sorted View, Search, Queue, and Summary!
        System.out.println("\n=== SORTED LOANS (Overdue First, Due Date Asc) ===");
        for (LoanRecord r : service.getSortedLoansView()) {
            System.out.printf("%s | ISBN: %s | Status: %s | Overdue: %b | Due: %s | Fine: PHP %.2f%n",
                    r.getLoanId(), r.getBook().getIsbn(), r.getStatus(), r.isOverdue(), r.getDueDate(), r.getEstimatedFine());
        }

        System.out.print("\nEnter keyword to search Book Title: ");
        String kw = in.nextLine().trim();
        System.out.println("Search Matches Found: " + service.searchBooksByTitle(kw).size());

        System.out.println("Processing Next Reservation in Queue: " + service.processNextReservation());
        System.out.println("\n=== SYSTEM SUMMARY ===");
        System.out.println(service.generateSummary());
        in.close();
    }
}