public abstract class ServiceRequest implements Prioritizable {
    private static int totalRequests = 0; // Shared class
    
    private String ticketId;
    private Student student;
    private String description;
    private int priority;

    public ServiceRequest(Student student, String description, int priority) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be blank.");
        }
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Priority must be between 1 and 5.");
        }
        
        this.student = student;
        this.description = description.trim();
        this.priority = priority;
        
        totalRequests++;
        this.ticketId = "TKT-" + totalRequests;
    }

    public Student getStudent() { return student; }
    public String getTicketId() { return ticketId; }
    public String getDescription() { return description; }
    
    @Override
    public int getPriority() { return priority; }

   // Abstract method 
    public abstract int estimateDays(); 
}