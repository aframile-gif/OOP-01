package model;

interface Prioritizable {
    int getPriority();
}

public abstract class ServiceRequest implements Prioritizable {
    public enum RequestCategory { ENROLLMENT, DOCUMENT, PAYMENT, ADVISING }
    public enum TicketStatus { OPEN, IN_PROGRESS, RESOLVED }

    private static int totalRequests = 0;
    private String ticketId;
    private Student student;
    private RequestCategory category;
    private String description;
    private int priority;
    private TicketStatus status;

    public ServiceRequest(Student student, RequestCategory category, String description, int priority) {
        if (student == null || description == null || description.isBlank()) {
            throw new IllegalArgumentException("Student and Description cannot be blank.");
        }
        this.student = student;
        this.category = category;
        this.description = description.trim();
        this.priority = priority;
        this.status = TicketStatus.OPEN;
        this.ticketId = "TKT-" + (++totalRequests);
    }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public Student getStudent() { return student; }
    public RequestCategory getCategory() { return category; }
    public String getDescription() { return description; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    @Override
    public int getPriority() { return priority; }

    public abstract int estimateDays();

    public static ServiceRequest create(Student s, RequestCategory cat, String desc, int pri) {
        return switch (cat) {
            case ENROLLMENT -> new EnrollmentRequest(s, desc, pri);
            case DOCUMENT   -> new DocumentRequest(s, desc, pri);
            case PAYMENT    -> new PaymentConcern(s, desc, pri);
            case ADVISING   -> new AdvisingRequest(s, desc, pri);
        };
    }

    // Helper used by JDBC DAO when reconstructing saved rows from MySQL
    public static ServiceRequest createFromDb(String ticketId, Student s, RequestCategory cat,
                                              String desc, int pri, TicketStatus status) {
        ServiceRequest req = create(s, cat, desc, pri);
        req.setTicketId(ticketId);
        req.setStatus(status);
        return req;
    }
}

class EnrollmentRequest extends ServiceRequest {
    public EnrollmentRequest(Student s, String d, int p) { super(s, RequestCategory.ENROLLMENT, d, p); }
    @Override public int estimateDays() { return Math.max(1, getPriority() == 5 ? 3 - 1 : 3); }
}

class DocumentRequest extends ServiceRequest {
    public DocumentRequest(Student s, String d, int p) { super(s, RequestCategory.DOCUMENT, d, p); }
    @Override public int estimateDays() { return Math.max(1, getPriority() == 5 ? 2 - 1 : 2); }
}

class PaymentConcern extends ServiceRequest {
    public PaymentConcern(Student s, String d, int p) { super(s, RequestCategory.PAYMENT, d, p); }
    @Override public int estimateDays() { return Math.max(1, getPriority() == 5 ? 1 - 1 : 1); }
}

class AdvisingRequest extends ServiceRequest {
    public AdvisingRequest(Student s, String d, int p) { super(s, RequestCategory.ADVISING, d, p); }
    @Override public int estimateDays() { return Math.max(1, getPriority() == 5 ? 4 - 1 : 4); }
}