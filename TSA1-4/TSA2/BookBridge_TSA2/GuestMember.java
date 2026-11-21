public class GuestMember extends Member {
    public GuestMember(String memberId, String name) {
        super(memberId, name);
    }
    @Override
    public int getMaximumLoanDays() { return 7; }
    @Override
    public double getDailyFine() { return 10.0; }
}