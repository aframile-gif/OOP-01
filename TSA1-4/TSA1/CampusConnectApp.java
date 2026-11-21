import java.util.Scanner;

public class CampusConnectApp {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String[] ids = new String[5], names = new String[5], cats = new String[5], desc = new String[5];
        int[] pris = new int[5], days = new int[5];
        int count = 0;

        System.out.println("--- CampusConnect Service Console ---");

        while (count < 5) {
            System.out.println("\nRequest #" + (count + 1));
            
            System.out.print("ID: "); String id = in.nextLine().trim();
            System.out.print("Name: "); String name = in.nextLine().trim();
            System.out.print("Category (ENROLLMENT, DOCUMENT, PAYMENT, ADVISING): "); String cat = in.nextLine().trim().toUpperCase();
            System.out.print("Description: "); String d = in.nextLine().trim();
            
            int pri = 0;
            try {
                System.out.print("Priority (1-5): "); 
                pri = Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) { 
               
            }

            // Validation
            if (id.isEmpty() || name.isEmpty() || d.isEmpty() || !validateCategory(cat) || pri < 1 || pri > 5) {
                System.out.println("Error: Invalid or blank input detected. Please re-enter the request.");
                continue; // Skips to the next loop iteration 
            }

           
            ids[count] = id; names[count] = name; cats[count] = cat; 
            pris[count] = pri; desc[count] = d;
            days[count] = estimateDays(cat, pri);

            printTicket(id, name, cat, pri, days[count]);
            count++;
        }
        
        printSummary(cats, pris, days);
        in.close();
    }

    public static boolean validateCategory(String c) {
        return c.equals("ENROLLMENT") || c.equals("DOCUMENT") || c.equals("PAYMENT") || c.equals("ADVISING");
    }

    public static int estimateDays(String c, int p) {
        int d = switch (c) {
            case "ENROLLMENT" -> 3; 
            case "DOCUMENT" -> 2;
            case "PAYMENT" -> 1; 
            case "ADVISING" -> 4; 
            default -> 0;
        };
        return Math.max(1, p == 5 ? d - 1 : d);
    }

    public static void printTicket(String id, String n, String c, int p, int d) {
        System.out.printf(">> TICKET: %s (%s) - %s [Pri: %d] -> %d day(s)%n", n, id, c, p, d);
    }

    public static void printSummary(String[] cats, int[] pris, int[] days) {
        int maxPri = 0, totalD = 0, e = 0, d = 0, p = 0, a = 0;
        
        for (int i = 0; i < 5; i++) {
            if (cats[i].equals("ENROLLMENT")) e++;
            if (cats[i].equals("DOCUMENT")) d++;
            if (cats[i].equals("PAYMENT")) p++;
            if (cats[i].equals("ADVISING")) a++;
            if (pris[i] > maxPri) maxPri = pris[i];
            totalD += days[i];
        }
        
        System.out.printf("\n=== SUMMARY ===%nEnroll: %d, Doc: %d, Pay: %d, Adv: %d%n", e, d, p, a);
        System.out.printf("Highest Priority Handled: %d | Avg Completion: %.1f days%n", maxPri, totalD / 5.0);
    }
}