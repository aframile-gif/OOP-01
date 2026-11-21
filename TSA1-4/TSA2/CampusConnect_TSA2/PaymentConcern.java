public class PaymentConcern extends ServiceRequest {
    public PaymentConcern(Student student, String description, int priority) {
        super(student, description, priority);
    }
    @Override
    public int estimateDays() {
        int base = 1; // Base 1, cannot drop below 1 even if priority 5
        return Math.max(1, getPriority() == 5 ? base - 1 : base); 
    }
}