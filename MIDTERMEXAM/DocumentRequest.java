
import java.time.LocalDate;

public abstract class DocumentRequest {
    // The only statuses a request may have
    public static final String[] VALID_STATUSES = {"Pending", "Processing", "Ready", "Released"};
 
    private final String requestId;
    private final Resident resident;        // HAS-A every request belongs to one resident
    private final LocalDate requestDate;
    private String status;
    private double fee;
 
    public DocumentRequest(String requestId, Resident resident) {
        if (resident == null) {
            throw new IllegalArgumentException("A request must belong to a resident.");
        }
        this.requestId = requestId;
        this.resident = resident;
        this.requestDate = LocalDate.now();
        this.status = "Pending";
        this.fee = 0.0;                      //free documents keep this value
    }
 
    // getters
    public String getRequestId() { return requestId; }
    public Resident getResident() { return resident; }
    public LocalDate getRequestDate() { return requestDate; }
    public String getStatus() { return status; }
    public double getFee() { return fee; }
 
    //  only this class and its subclasses may change the fee
    protected void setFee(double fee) {
        if (fee < 0) {
            throw new IllegalArgumentException("Fee cannot be negative.");
        }
        this.fee = fee;
    }
 
    /** Changes the status only if the new value is one of the valid statuses. */
    public void updateStatus(String newStatus) {
        for (String valid : VALID_STATUSES) {
            if (valid.equalsIgnoreCase(newStatus)) {
                this.status = valid;         // store the properly capitalised
                return;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + newStatus
                + " (use Pending, Processing, Ready, or Released)");
    }
 
    // Subclasses override and call super.processRequest()
    public void processRequest() {
        if (status.equals("Pending")) {
            status = "Processing";
            System.out.println("Request " + requestId + " is now Processing.");
        } else {
            System.out.println("Request " + requestId + " is already " + status + ".");
        }
    }
 
    public void displayRequest() {
        System.out.println("-----------------------------------------");
        System.out.println("Request ID : " + requestId);
        System.out.println("Document   : " + getDocumentType());      // polymorphic call
        System.out.println("Resident   : " + resident.getFullName());
        System.out.println("Date       : " + requestDate);
        System.out.println("Details    : " + getSpecificDetails());   // polymorphic call
        System.out.println("Status     : " + status);
        System.out.printf("Fee        : PHP %.2f%n", fee);
    }
 
    // every subclass  provide its own version 
    public abstract String getDocumentType();
    public abstract String getSpecificDetails();
    // Sample list of the documents a resident should bring 
    public abstract String getRequirements();
}
