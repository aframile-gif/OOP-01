import java.util.ArrayList;
 public class RequestManager {
    private final ArrayList<Resident> residents = new ArrayList<>();
    private final ArrayList<DocumentRequest> requests = new ArrayList<>();
 
    // residents 
    public void addResident(Resident resident) {
        residents.add(resident);
    }
 
    // Returns the matching resident or null if none exists 
    public Resident findResident(String residentId) {
        for (Resident r : residents) {
            if (r.getResidentId().equalsIgnoreCase(residentId)) {
                return r;
            }
        }
        return null;
    }
 
    public void displayAllResidents() {
        for (Resident r : residents) {
            System.out.println("  " + r);
        }
    }
 
    public int getResidentCount() { return residents.size(); }
 
    //  requests 
 
    public void addRequest(DocumentRequest request) {
        requests.add(request);
    }
 
    // Returns the matching request, or null if none exists 
    public DocumentRequest findRequest(String requestId) {
        for (DocumentRequest r : requests) {
            if (r.getRequestId().equalsIgnoreCase(requestId)) {
                return r;
            }
        }
        return null;
    }
 
    // Returns true if the request exists and the status was updated
    public boolean updateRequestStatus(String requestId, String newStatus) {
        DocumentRequest r = findRequest(requestId);
        if (r == null) {
            return false;
        }
        r.updateStatus(newStatus);   // IllegalArgumentException for bad status
        return true;
    }
 
  public void displayAllRequests() {
        if (requests.isEmpty()) {
            System.out.println("No requests recorded yet.");
            return;
        }
        for (DocumentRequest r : requests) {
            r.displayRequest();      
        }
        
        double totalRevenue = 0;
        for (DocumentRequest r : requests) {
            totalRevenue += r.getFee();
        }
        
        System.out.println("-----------------------------------------");
        System.out.println("Total requests: " + requests.size());
        System.out.println("Total Revenue: PHP " + totalRevenue);
    } }
