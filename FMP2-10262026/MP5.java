import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

class Book {
    private int id;
    private String title;
    private int available;
    private int borrowed;
    private LinkedList<String> waitingList;
    private HashSet<String> reservationKeys;

    public Book(int id, String title, int available) {
        this.id = id;
        this.title = title;
        this.available = available;
        this.borrowed = 0;
        this.waitingList = new LinkedList<>();
        this.reservationKeys = new HashSet<>();
    }

    public int getId() { return id; }
    public String getTitle() { return title; }

    public String getStatus(boolean capitalize) {
        String status = "available " + available + ", borrowed " + borrowed;
        return capitalize ? "A" + status.substring(1) : status;
    }

    public String borrowBook() {
        if (available > 0) {
            available--;
            borrowed++;
            return getStatus(true);
        } else {
            return "No copies available";
        }
    }

    public String reserveBook(String patron) {
        if (reservationKeys.contains(patron)) {
            return "Duplicate rejected";
        }
        reservationKeys.add(patron);
        waitingList.add(patron);
        
        if (waitingList.size() == 1) {
            return patron + " added to waiting list";
        } else {
            return "Waiting: " + String.join(", ", waitingList);
        }
    }

    public String returnBook() {
        if (borrowed > 0) {
            borrowed--;
            available++;
            
            if (!waitingList.isEmpty()) {
                String nextPatron = waitingList.poll();
                reservationKeys.remove(nextPatron);
                available--;
                borrowed++;
                
                String result = "Copy assigned to " + nextPatron + "; " + getStatus(false);
                if (!waitingList.isEmpty()) {
                    result += "; " + String.join(", ", waitingList) + " waiting";
                }
                return result;
            }
            return getStatus(true);
        } else {
            return "No copies to return";
        }
    }

    public String showWaiting() {
        if (waitingList.isEmpty()) {
            return "No one waiting";
        } else if (waitingList.size() == 1) {
            return waitingList.getFirst() + " only";
        } else {
            return String.join(", ", waitingList);
        }
    }
}

public class MP5 {
    private static HashMap<Integer, Book> library = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Seed initial data
        library.put(1, new Book(1, "Java Basics", 1));
        library.put(2, new Book(2, "Algorithms", 0));
        library.put(3, new Book(3, "Databases", 2));

        while (true) {
            System.out.println("\nMenu: 1 Borrow | 2 Reserve | 3 Return | 4 Find | 5 Show waiting | 0 Exit");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("0")) {
                System.out.println("Exiting...");
                break;
            }

            try {
                String[] parts;
                int id;

                switch (choice) {
                    case "1":
                        System.out.print("Enter ID and Name to borrow (e.g., 1 Ana): ");
                        parts = scanner.nextLine().trim().split("\\s+");
                        id = Integer.parseInt(parts[0]);
                        if (library.containsKey(id)) {
                            System.out.println(library.get(id).borrowBook());
                        } else {
                            System.out.println("Book not found");
                        }
                        break;

                    case "2":
                        System.out.print("Enter ID and Name to reserve (e.g., 1 Ben): ");
                        parts = scanner.nextLine().trim().split("\\s+");
                        id = Integer.parseInt(parts[0]);
                        if (library.containsKey(id)) {
                            System.out.println(library.get(id).reserveBook(parts[1]));
                        } else {
                            System.out.println("Book not found");
                        }
                        break;

                    case "3":
                        System.out.print("Enter ID to return: ");
                        id = Integer.parseInt(scanner.nextLine().trim());
                        if (library.containsKey(id)) {
                            System.out.println(library.get(id).returnBook());
                        } else {
                            System.out.println("Book not found");
                        }
                        break;

                    case "4":
                        System.out.print("Enter ID to find: ");
                        id = Integer.parseInt(scanner.nextLine().trim());
                        if (library.containsKey(id)) {
                            System.out.println(library.get(id).getStatus(true));
                        } else {
                            System.out.println("Book not found");
                        }
                        break;

                    case "5":
                        System.out.print("Enter ID to show waiting list: ");
                        id = Integer.parseInt(scanner.nextLine().trim());
                        if (library.containsKey(id)) {
                            System.out.println(library.get(id).showWaiting());
                        } else {
                            System.out.println("Book not found");
                        }
                        break;

                    default:
                        System.out.println("Invalid menu option.");
                }
            } catch (Exception e) {
                // Instantly catches malformed inputs (like missing names or text instead of numbers)
                System.out.println("Rejected; invalid input.");
            }
        }
    }
}
