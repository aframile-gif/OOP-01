package model;

public class Reservation {
    private static int resCount = 0;
    private String reservationId;
    private Member member;
    private LibraryBook book;

    public Reservation(Member member, LibraryBook book) {
        this.reservationId = "RES-" + (++resCount);
        this.member = member;
        this.book = book;
    }

    public String getReservationId() { return reservationId; }
    public Member getMember() { return member; }
    public LibraryBook getBook() { return book; }
}