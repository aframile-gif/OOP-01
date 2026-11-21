package model;

interface Notifiable {
    void sendNotification(String message);
}

public abstract class Member implements Notifiable {
    public enum MembershipType { STUDENT, FACULTY, GUEST }

    private String memberId;
    private String name;
    private MembershipType type;

    public Member(String memberId, String name, MembershipType type) {
        if (memberId == null || memberId.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("Member ID and Name cannot be blank.");
        }
        this.memberId = memberId.trim();
        this.name = name.trim();
        this.type = type;
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public MembershipType getType() { return type; }

    public abstract int getMaximumLoanDays();
    public abstract double getDailyFine();

    @Override
    public void sendNotification(String message) {
        System.out.println("[NOTICE to " + name + "]: " + message);
    }

    // Factory helper so other packages can create Student, Faculty, or Guest easily
    public static Member create(String id, String name, MembershipType type) {
        return switch (type) {
            case STUDENT -> new StudentMember(id, name);
            case FACULTY -> new FacultyMember(id, name);
            case GUEST   -> new GuestMember(id, name);
        };
    }
}

class StudentMember extends Member {
    public StudentMember(String id, String name) { super(id, name, MembershipType.STUDENT); }
    @Override public int getMaximumLoanDays() { return 14; }
    @Override public double getDailyFine() { return 5.0; }
}

class FacultyMember extends Member {
    public FacultyMember(String id, String name) { super(id, name, MembershipType.FACULTY); }
    @Override public int getMaximumLoanDays() { return 30; }
    @Override public double getDailyFine() { return 2.0; }
}

class GuestMember extends Member {
    public GuestMember(String id, String name) { super(id, name, MembershipType.GUEST); }
    @Override public int getMaximumLoanDays() { return 7; }
    @Override public double getDailyFine() { return 10.0; }
}