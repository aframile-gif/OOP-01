package model;

public class Reservation {
    private static int resCount = 0;
    private String reservationId;
    private Member member;
    private LibraryBook book;
    private String status = "PENDING";

   
    public Reservation(Member member, LibraryBook book) {
        this.reservationId = "RES-" + (++resCount);
        this.member = member;
        this.book = book;
        this.status = "PENDING";
    }

    
    public Reservation(String reservationId, Member member, LibraryBook book, String status) {
        this.reservationId = reservationId;
        this.member = member;
        this.book = book;
        this.status = status;
    }

    public String getReservationId() { return reservationId; }
    public Member getMember() { return member; }
    public LibraryBook getBook() { return book; }
    public String getStatus() { return status; }
}