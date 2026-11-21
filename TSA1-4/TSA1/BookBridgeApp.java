import java.util.Scanner;

public class BookBridgeApp {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String[] ids = new String[5], names = new String[5], types = new String[5];
        String[] isbns = new String[5], titles = new String[5], outcomes = new String[5];
        int[] reqDays = new int[5], lateDays = new int[5], fines = new int[5];
        boolean[] avail = new boolean[5];
        int count = 0;

        System.out.println("--- BookBridge Library System ---");

        while (count < 5) {
            System.out.println("\nTransaction #" + (count + 1));
            
            System.out.print("ID: "); String id = in.nextLine().trim();
            System.out.print("Name: "); String name = in.nextLine().trim();
            System.out.print("Type (STUDENT, FACULTY, GUEST): "); String type = in.nextLine().trim().toUpperCase();
            System.out.print("ISBN: "); String isbn = in.nextLine().trim();
            System.out.print("Title: "); String title = in.nextLine().trim();
            
            int rDays = -1, lDays = -1;
            boolean isAvail = false;
            
            try {
                System.out.print("Requested Days (1-30): "); rDays = Integer.parseInt(in.nextLine().trim());
                System.out.print("Available? (true/false): "); isAvail = Boolean.parseBoolean(in.nextLine().trim());
                System.out.print("Late Days: "); lDays = Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                
            }

            // Validation
            if (id.isEmpty() || name.isEmpty() || isbn.isEmpty() || title.isEmpty() || 
                !validateType(type) || rDays < 1 || rDays > 30 || lDays < 0) {
                System.out.println("Error: Invalid or blank input detected. Please re-enter the transaction.");
                continue;
            }

            
            String out = classifyRequest(isAvail, rDays, getMaxDays(type));
            int fine = out.equals("APPROVED") ? (lDays * getFine(type)) : 0;

            // arrays
            ids[count] = id; names[count] = name; types[count] = type; 
            isbns[count] = isbn; titles[count] = title;
            reqDays[count] = rDays; avail[count] = isAvail; 
            lateDays[count] = lDays; outcomes[count] = out; fines[count] = fine;

            System.out.printf(">> Result: %s | Est. Fine: PHP %d%n", out, fine);
            count++;
        }
        
        printSummary(outcomes, reqDays, fines);
        in.close();
    }

    public static boolean validateType(String t) {
        return t.equals("STUDENT") || t.equals("FACULTY") || t.equals("GUEST");
    }

    public static int getMaxDays(String t) {
        return switch (t) { case "STUDENT" -> 14; case "FACULTY" -> 30; case "GUEST" -> 7; default -> 0; };
    }

    public static int getFine(String t) {
        return switch (t) { case "STUDENT" -> 5; case "FACULTY" -> 2; case "GUEST" -> 10; default -> 0; };
    }

    public static String classifyRequest(boolean avail, int req, int max) {
        if (!avail) return "RESERVED";
        return (req <= max) ? "APPROVED" : "DENIED";
    }

    public static void printSummary(String[] outs, int[] reqs, int[] fines) {
        int app = 0, res = 0, den = 0, maxReq = 0, totalFine = 0;
        
        for (int i = 0; i < 5; i++) {
            if (outs[i].equals("APPROVED")) app++;
            if (outs[i].equals("RESERVED")) res++;
            if (outs[i].equals("DENIED")) den++;
            if (reqs[i] > maxReq) maxReq = reqs[i];
            totalFine += fines[i];
        }
        
        System.out.printf("\n=== SYSTEM SUMMARY ===%nApprovals: %d, Reservations: %d, Denials: %d%n", app, res, den);
        System.out.printf("Greatest Requested Days: %d | Total Fines: PHP %d%n", maxReq, totalFine);
    }
}
