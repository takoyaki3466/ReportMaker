package org.takoyaki.reportmaker.model;

public final class TextBlock implements ReportBlock {
    private String text;

    public TextBlock(String text) {
        this.text = text == null ? "" : text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text == null ? "" : text;
    }

    @Override
    public BlockType getType() {
        return BlockType.TEXT;
    }
}
