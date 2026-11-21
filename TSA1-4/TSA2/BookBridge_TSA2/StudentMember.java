public class StudentMember extends Member {
    public StudentMember(String memberId, String name) {
        super(memberId, name);
    }
    @Override
    public int getMaximumLoanDays() { return 14; }
    @Override
    public double getDailyFine() { return 5.0; }
}
