package model;

public class LibraryBook {
    public enum BookStatus { AVAILABLE, CHECKED_OUT, RESERVED }

    private String isbn;
    private String title;
    private BookStatus status;

    public LibraryBook(String isbn, String title, boolean available) {
        if (isbn == null || isbn.isBlank() || title == null || title.isBlank()) {
            throw new IllegalArgumentException("ISBN and Title cannot be blank.");
        }
        this.isbn = isbn.trim();
        this.title = title.trim();
        this.status = available ? BookStatus.AVAILABLE : BookStatus.CHECKED_OUT;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public BookStatus getStatus() { return status; }
    public boolean isAvailable() { return status == BookStatus.AVAILABLE; }

    public void setStatus(BookStatus status) { this.status = status; }
    public void markCheckedOut() { this.status = BookStatus.CHECKED_OUT; }
    public void reserve() { this.status = BookStatus.RESERVED; }
    public void markReturned() { this.status = BookStatus.AVAILABLE; }
}