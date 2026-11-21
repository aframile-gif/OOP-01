public abstract class Member implements Notifiable {
    private String memberId;
    private String name;

    public Member(String memberId, String name) {
        if (memberId == null || memberId.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID and Name cannot be blank.");
        }
        this.memberId = memberId.trim();
        this.name = name.trim();
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }

    // Abstract methods force subclasses to define their specific rules
    public abstract int getMaximumLoanDays();
    public abstract double getDailyFine();

    @Override
    public void sendNotification(String message) {
        System.out.println("[NOTICE to " + this.name + "]: " + message);
    }
}