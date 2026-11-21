public class FacultyMember extends Member {
    public FacultyMember(String memberId, String name) {
        super(memberId, name);
    }
    @Override
    public int getMaximumLoanDays() { return 30; }
    @Override
    public double getDailyFine() { return 2.0; }
}