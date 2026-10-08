package org.takoyaki.reportmaker.model;

public final class SubsectionBlock implements ReportBlock {
    private String title;
    private String importedNumber;

    public SubsectionBlock(String title) {
        this(title, "");
    }

    public SubsectionBlock(String title, String importedNumber) {
        this.title = title == null ? "" : title;
        this.importedNumber = importedNumber == null ? "" : importedNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title;
    }

    public String getImportedNumber() {
        return importedNumber;
    }

    public void setImportedNumber(String importedNumber) {
        this.importedNumber = importedNumber == null ? "" : importedNumber;
    }

    @Override
    public BlockType getType() {
        return BlockType.SUBSECTION;
    }
}
