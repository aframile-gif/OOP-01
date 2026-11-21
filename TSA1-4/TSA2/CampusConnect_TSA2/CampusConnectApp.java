import java.util.Scanner;

public class CampusConnectApp {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        ServiceRequest[] requests = new ServiceRequest[5];
        int count = 0;

        System.out.println("--- CampusConnect OOP Service Console ---");

        while (count < 5) {
            System.out.println("\nRequest #" + (count + 1));
            try {
                System.out.print("ID: "); String id = in.nextLine();
                System.out.print("Name: "); String name = in.nextLine();
                System.out.print("Category (ENROLLMENT, DOCUMENT, PAYMENT, ADVISING): "); 
                String cat = in.nextLine().toUpperCase().trim();
                System.out.print("Description: "); String desc = in.nextLine();
                System.out.print("Priority (1-5): "); int pri = Integer.parseInt(in.nextLine());

                // Encapsulate Student
                Student student = new Student(id, name);
                
                //  Polymorphic 
                ServiceRequest req;
                switch (cat) {
                    case "ENROLLMENT" -> req = new EnrollmentRequest(student, desc, pri);
                    case "DOCUMENT" -> req = new DocumentRequest(student, desc, pri);
                    case "PAYMENT" -> req = new PaymentConcern(student, desc, pri);
                    case "ADVISING" -> req = new AdvisingRequest(student, desc, pri);
                    default -> throw new IllegalArgumentException("Invalid Category.");
                }

                // Store object in polymorphic array
                requests[count] = req;
                
                System.out.printf(">> TICKET [%s]: %s (%s) - %s [Pri: %d] -> %d day(s)%n", 
                    req.getTicketId(), req.getStudent().getName(), req.getStudent().getId(), 
                    cat, req.getPriority(), req.estimateDays());
                count++;

            } catch (Exception e) {
                System.out.println("Error: Invalid input detected (" + e.getMessage() + "). Please re-enter.");
            }
        }
        
        printSummary(requests);
        in.close();
    }

    private static void printSummary(ServiceRequest[] requests) {
        int e = 0, d = 0, p = 0, a = 0, maxPri = 0, totalDays = 0;

        for (ServiceRequest req : requests) {
            if (req instanceof EnrollmentRequest) e++;
            else if (req instanceof DocumentRequest) d++;
            else if (req instanceof PaymentConcern) p++;
            else if (req instanceof AdvisingRequest) a++;
            
            if (req.getPriority() > maxPri) maxPri = req.getPriority();
            totalDays += req.estimateDays();
        }

        System.out.println("\n=== SYSTEM SUMMARY ===");
        System.out.printf("Enroll: %d, Doc: %d, Pay: %d, Adv: %d%n", e, d, p, a);
        System.out.printf("Highest Priority Handled: %d | Avg Completion: %.1f days%n", 
            maxPri, totalDays / 5.0);
    }
}