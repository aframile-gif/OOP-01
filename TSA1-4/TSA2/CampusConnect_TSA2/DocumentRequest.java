public class DocumentRequest extends ServiceRequest {
    public DocumentRequest(Student student, String description, int priority) {
        super(student, description, priority);
    }
    
    @Override
    public int estimateDays() {
        int base = 2;
        return Math.max(1, getPriority() == 5 ? base - 1 : base);
    }
}