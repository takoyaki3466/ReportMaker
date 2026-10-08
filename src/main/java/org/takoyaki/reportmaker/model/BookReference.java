package org.takoyaki.reportmaker.model;

public final class BookReference implements Reference {
    private String author;
    private String title;
    private String publisher;
    private String publicationYear;

    public BookReference(String author, String title, String publisher, String publicationYear) {
        this.author = normalize(author);
        this.title = normalize(title);
        this.publisher = normalize(publisher);
        this.publicationYear = normalize(publicationYear);
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = normalize(author); }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = normalize(title); }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = normalize(publisher); }
    public String getPublicationYear() { return publicationYear; }
    public void setPublicationYear(String publicationYear) { this.publicationYear = normalize(publicationYear); }

    @Override public ReferenceType getType() { return ReferenceType.BOOK; }
    @Override public String getDisplayText() { return author + ": \"" + title + "\", " + publisher + " (" + publicationYear + ")"; }

    private String normalize(String value) { return value == null ? "" : value; }
}
