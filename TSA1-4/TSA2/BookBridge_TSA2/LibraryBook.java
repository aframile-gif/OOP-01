public class LibraryBook {
    private String isbn;
    private String title;
    private boolean available;

    public LibraryBook(String isbn, String title, boolean available) {
        this.isbn = isbn;
        this.title = title;
        this.available = available;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public boolean isAvailable() { return available; }

    public void reserve() { this.available = false; }
    public void markReturned() { this.available = true; }
}

