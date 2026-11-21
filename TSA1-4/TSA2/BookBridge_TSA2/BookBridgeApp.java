import java.util.Scanner;

public class BookBridgeApp {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        LoanRecord[] transactions = new LoanRecord[5];
        int count = 0;

        System.out.println("--- BookBridge OOP Library System ---");

        while (count < 5) {
            System.out.println("\nTransaction #" + (count + 1));
            try {
                System.out.print("ID: "); String id = in.nextLine();
                System.out.print("Name: "); String name = in.nextLine();
                System.out.print("Type (STUDENT, FACULTY, GUEST): "); String type = in.nextLine().toUpperCase();
                System.out.print("ISBN: "); String isbn = in.nextLine();
                System.out.print("Title: "); String title = in.nextLine();
                System.out.print("Requested Days (1-30): "); int rDays = Integer.parseInt(in.nextLine());
                System.out.print("Available? (true/false): "); boolean avail = Boolean.parseBoolean(in.nextLine());
                System.out.print("Late Days: "); int lDays = Integer.parseInt(in.nextLine());

                // 1. Polymorphic Object Creation
                Member member;
                switch (type) {
                    case "STUDENT" -> member = new StudentMember(id, name);
                    case "FACULTY" -> member = new FacultyMember(id, name);
                    case "GUEST" -> member = new GuestMember(id, name);
                    default -> throw new IllegalArgumentException("Invalid Member Type.");
                }

                // Encapsulated
                LibraryBook book = new LibraryBook(isbn, title, avail);
                LoanRecord loan = new LoanRecord(member, book, rDays, lDays);

                if (loan.getStatus().equals("RESERVED")) {
                    Reservation res = new Reservation(member, book);
                    book.reserve();
                }

                transactions[count] = loan;
                System.out.printf(">> Result: %s | Est. Fine: PHP %.2f%n", loan.getStatus(), loan.getEstimatedFine());
                count++;

            } catch (Exception e) {
                System.out.println("Error: Invalid input detected (" + e.getMessage() + "). Please re-enter.");
            }
        }
        printSummary(transactions);
        in.close();
    }

    private static void printSummary(LoanRecord[] records) {
        int app = 0, res = 0, den = 0, maxReq = 0;
        double totalFine = 0;

        for (LoanRecord record : records) {
            if (record.getStatus().equals("APPROVED")) app++;
            if (record.getStatus().equals("RESERVED")) res++;
            if (record.getStatus().equals("DENIED")) den++;
            
            if (record.getRequestedDays() > maxReq) maxReq = record.getRequestedDays();
            totalFine += record.getEstimatedFine();
        }

        System.out.println("\n=== SYSTEM SUMMARY ===");
        System.out.printf("Total Processed: %d%n", LoanRecord.getTotalLoans());
        System.out.printf("Approvals: %d, Reservations: %d, Denials: %d%n", app, res, den);
        System.out.printf("Greatest Requested Days: %d | Total Fines: PHP %.2f%n", maxReq, totalFine);
    }
}
