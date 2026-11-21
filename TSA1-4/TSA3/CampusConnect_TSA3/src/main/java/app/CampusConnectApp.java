package app;

import exception.*;
import model.*;
import model.ServiceRequest.RequestCategory;
import service.CampusService;
import java.util.Scanner;

public class CampusConnectApp {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        CampusService service = new CampusService();
        int count = 0;

        System.out.println("--- CampusConnect TSA3 Service Console ---");

        while (count < 5) {
            System.out.println("\nRequest #" + (count + 1));
            try {
                System.out.print("ID: "); String id = in.nextLine().trim();
                System.out.print("Name: "); String name = in.nextLine().trim();
                System.out.print("Category (ENROLLMENT, DOCUMENT, PAYMENT, ADVISING): ");
                String catStr = in.nextLine().trim().toUpperCase();
                System.out.print("Description: "); String desc = in.nextLine().trim();
                System.out.print("Priority (1-5): "); int pri = Integer.parseInt(in.nextLine().trim());

                RequestCategory cat = RequestCategory.valueOf(catStr);

                // Register student in Map & Set (if new)
                try {
                    service.registerStudent(new Student(id, name));
                } catch (DuplicateTicketException e) {
                    System.out.println("[Info] Existing student found: " + id);
                }

                ServiceRequest req = service.submitRequest(id, cat, desc, pri);
                System.out.printf(">> TICKET [%s]: %s (%s) - %s [Pri: %d] -> %d day(s)%n",
                        req.getTicketId(), req.getStudent().getName(), req.getStudent().getId(),
                        cat, req.getPriority(), req.estimateDays());
                count++;

            } catch (DuplicateTicketException | InvalidPriorityException | TicketNotFoundException e) {
                System.out.println("Domain Error: " + e.getMessage() + ". Please re-enter the request.");
            } catch (Exception e) {
                System.out.println("Error: Invalid or blank input detected (" + e.getMessage() + "). Please re-enter.");
            }
        }

        System.out.println("\n=== SORTED REQUESTS (Highest Priority First) ===");
        for (ServiceRequest r : service.getSortedRequestsView()) {
            System.out.printf("%s | %s | Cat: %s | Pri: %d | Est: %d day(s) | Status: %s%n",
                    r.getTicketId(), r.getStudent().getName(), r.getCategory(),
                    r.getPriority(), r.estimateDays(), r.getStatus());
        }

        System.out.print("\nEnter keyword to search Description: ");
        String kw = in.nextLine().trim();
        System.out.println("Search Matches Found: " + service.searchByKeyword(kw).size());

        System.out.println("Processed Next Ticket in Queue: " + service.processNextRequest().map(ServiceRequest::getTicketId).orElse("Empty"));
        System.out.println("\n=== SUMMARY ===");
        System.out.println(service.generateSummary());
        in.close();
    }
}