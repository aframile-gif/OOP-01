import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;

class Ticket {
    private int id;
    private String customerName;
    private String issue;

    public Ticket(int id, String customerName, String issue) {
        this.id = id;
        this.customerName = customerName;
        this.issue = issue;
    }

    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getIssue() { return issue; }

    @Override
    public String toString() {
        return "Ticket " + id + " " + customerName;
    }
}

public class MP3 {
    private static Queue<Ticket> queue = new ArrayDeque<>();
    private static Scanner scanner = new Scanner(System.in);
    private static int ticketCounter = 1;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nMenu: 1 New ticket | 2 Serve next | 3 Preview next | 4 Show queue | 0 Exit");
            System.out.print("Select an option: ");
            
            String choice = scanner.nextLine().trim();

            if (choice.equals("0")) {
                System.out.println("Exiting...");
                break;
            }

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Enter Name and Issue (e.g., Ana Login): ");
                        String[] parts = scanner.nextLine().trim().split("\\s+", 2);
                        if (parts.length < 2) {
                            System.out.println("Rejected; invalid input.");
                            break;
                        }
                        
                        Ticket newTicket = new Ticket(ticketCounter++, parts[0], parts[1]);
                        queue.offer(newTicket);
                        System.out.println("Ticket " + newTicket.getId() + " created");
                        break;

                    case "2":
                        Ticket served = queue.poll();
                        if (served != null) {
                            System.out.println("Ticket " + served.getId() + " served");
                        } else {
                            System.out.println("No pending tickets");
                        }
                        break;

                    case "3":
                        Ticket next = queue.peek();
                        if (next != null) {
                            System.out.println(next.toString());
                        } else {
                            System.out.println("No pending tickets");
                        }
                        break;

                    case "4":
                        if (queue.isEmpty()) {
                            System.out.println("No pending tickets");
                        } else {
                            ArrayList<String> ticketIds = new ArrayList<>();
                            for (Ticket t : queue) {
                                ticketIds.add(String.valueOf(t.getId()));
                            }
                            System.out.println("Tickets " + String.join(", ", ticketIds));
                        }
                        break;

                    default:
                        System.out.println("Invalid menu option.");
                }
            } catch (Exception e) {
                System.out.println("Rejected; invalid input.");
            }
        }
        scanner.close();
    }
}
