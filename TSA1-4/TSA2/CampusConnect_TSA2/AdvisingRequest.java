public class AdvisingRequest extends ServiceRequest {
    public AdvisingRequest(Student student, String description, int priority) {
        super(student, description, priority);
    }
    @Override
    public int estimateDays() {
        int base = 4;
        return Math.max(1, getPriority() == 5 ? base - 1 : base);
    }
}