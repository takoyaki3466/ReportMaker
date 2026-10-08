package org.takoyaki.reportmaker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class ReportSection implements Serializable {
    private SectionType type;
    private String title;
    private final List<ReportBlock> blocks = new ArrayList<>();

    public ReportSection(SectionType type) {
        this(type, type.getDisplayName());
    }

    public ReportSection(SectionType type, String title) {
        this.type = type;
        this.title = title == null ? "" : title;
    }

    public SectionType getType() {
        return type;
    }

    public void setType(SectionType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title;
    }

    public List<ReportBlock> getBlocks() {
        return blocks;
    }

    @Override
    public String toString() {
        return title;
    }
}
