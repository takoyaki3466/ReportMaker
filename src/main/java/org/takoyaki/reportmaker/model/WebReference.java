package org.takoyaki.reportmaker.model;

public final class WebReference implements Reference {
    private String author;
    private String pageTitle;
    private String url;
    private String accessedDate;

    public WebReference(String author, String pageTitle, String url, String accessedDate) {
        this.author = normalize(author);
        this.pageTitle = normalize(pageTitle);
        this.url = normalize(url);
        this.accessedDate = normalize(accessedDate);
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = normalize(author); }
    public String getPageTitle() { return pageTitle; }
    public void setPageTitle(String pageTitle) { this.pageTitle = normalize(pageTitle); }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = normalize(url); }
    public String getAccessedDate() { return accessedDate; }
    public void setAccessedDate(String accessedDate) { this.accessedDate = normalize(accessedDate); }

    @Override public ReferenceType getType() { return ReferenceType.WEB; }
    @Override public String getDisplayText() { return author + ": \"" + pageTitle + "\" " + url; }

    private String normalize(String value) { return value == null ? "" : value; }
}
