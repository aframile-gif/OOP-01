import java.util.Scanner;
 
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final RequestManager manager = new RequestManager();
    private static int residentCounter = 1;
    private static int requestCounter = 1;
 
    public static void main(String[] args) {
        boolean running = true;
        while (running) {                       // menu loop
            showMenu();
            int choice = readInt("Enter choice: ", 0, 7);
            switch (choice) {
                case 1: registerResident(); break;
                case 2: createRequest(); break;
                case 3: manager.displayAllRequests(); break;
                case 4: searchRequest(); break;
                case 5: processRequest(); break;
                case 6: changeStatus(); break;
                case 7: viewRequirements(); break;
                case 0:
                    System.out.println("Thank you. Goodbye!");
                    running = false;
                    break;
            }
        }
    }
 
    private static void showMenu() {
        System.out.println();
        System.out.println("===== BARANGAY DOCUMENT REQUEST SYSTEM =====");
        System.out.println("1. Register resident");
        System.out.println("2. Create document request");
        System.out.println("3. Display all requests");
        System.out.println("4. Search request by ID");
        System.out.println("5. Process a request");
        System.out.println("6. Update request status");
        System.out.println("7. View requirements of a request");
        System.out.println("0. Exit");
    }
 
  
    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
 
    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("This field cannot be empty.");
        }
    }
 
  
    private static void registerResident() {
        System.out.println("--- Register Resident ---");
        String name = readText("Full name: ");
        String address = readText("Address: ");
        String civil = readText("Civil status: ");
        while (true) {
            String contact = readText("Contact number (11 digits): ");
            try {
                String id = String.format("RES-%03d", residentCounter);
                Resident r = new Resident(id, name, address, contact, civil);
                manager.addResident(r);
                residentCounter++;
                System.out.println("Resident registered with ID " + id + ".");
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
 
    private static Resident chooseResident() {
        if (manager.getResidentCount() == 0) {
            System.out.println("No residents registered yet. Register a resident first.");
            return null;
        }
        System.out.println("Registered residents:");
        manager.displayAllResidents();
        String id = readText("Enter resident ID: ");
        Resident found = manager.findResident(id);
        if (found == null) {
            System.out.println("Resident not found.");
        }
        return found;
    }
 
    private static void createRequest() {
        System.out.println("--- Create Document Request ---");
        Resident resident = chooseResident();
        if (resident == null) {
            return;
        }
        System.out.println("1. Barangay Clearance");
        System.out.println("2. Certificate of Residency");
        System.out.println("3. Certificate of Indigency");
        System.out.println("4. Business Clearance");
        int type = readInt("Document type: ", 1, 4);
        String requestId = String.format("REQ-%03d", requestCounter);
 
        try {
            DocumentRequest request;               // one reference type for all four 
            switch (type) {
                case 1:
                    request = new BarangayClearance(requestId, resident, readText("Purpose: "));
                    break;
                case 2:
                    request = new CertificateOfResidency(requestId, resident,
                            readInt("Years of residency (1-120): ", 1, 120));
                    break;
                case 3:
                    request = new CertificateOfIndigency(requestId, resident, readText("Purpose: "));
                    break;
                default:
                    request = new BusinessClearance(requestId, resident,
                            readText("Business name: "), readText("Business type: "),
                            readText("Business address: "));
                    break;
            }
            manager.addRequest(request);
            requestCounter++;
            System.out.println("Request created: " + requestId);
            showFee(request);
            System.out.println("Requirements: " + request.getRequirements());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage() + " Request was not created.");
        }
    }
 
    
    private static void showFee(DocumentRequest request) {
        if (request instanceof FeeApplicable) {
            FeeApplicable feeDoc = (FeeApplicable) request;   // FeeApplicable view
            System.out.printf("Fee (calculated): PHP %.2f%n", feeDoc.calculateFee());
        } else {
            System.out.printf("%s is free. Fee: PHP %.2f%n",
                    request.getDocumentType(), request.getFee());
        }
    }

 
    private static void searchRequest() {
        String id = readText("Enter request ID: ");
        DocumentRequest r = manager.findRequest(id);
        if (r == null) {
            System.out.println("Request not found.");
        } else {
            r.displayRequest();
        }
    }
 
    private static void processRequest() {
        String id = readText("Enter request ID to process: ");
        DocumentRequest r = manager.findRequest(id);
        if (r == null) {
            System.out.println("Request not found.");
        } else {
            r.processRequest();                    // runs the subclass's overridden version
        }
    }
 
    private static void changeStatus() {
        String id = readText("Enter request ID: ");
        String status = readText("New status (Pending/Processing/Ready/Released): ");
        try {
            if (manager.updateRequestStatus(id, status)) {
                System.out.println("Status updated.");
            } else {
                System.out.println("Request not found.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
 
    private static void viewRequirements() {
        String id = readText("Enter request ID: ");
        DocumentRequest r = manager.findRequest(id);
        if (r == null) {
            System.out.println("Request not found.");
        } else {
            System.out.println(r.getDocumentType() + " requirements (sample): " + r.getRequirements());
        }
    }
}
