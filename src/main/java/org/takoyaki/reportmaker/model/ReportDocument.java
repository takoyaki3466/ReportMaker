package org.takoyaki.reportmaker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class ReportDocument implements Serializable {
    private String title = "";
    private String englishTitle = "";
    private final List<ReportSection> preparationSections = new ArrayList<>();
    private final List<ReportSection> mainSections = new ArrayList<>();
    private final List<Equipment> equipment = new ArrayList<>();
    private final List<ReportFigure> figures = new ArrayList<>();
    private final List<ReportEquation> equations = new ArrayList<>();
    private final List<Reference> references = new ArrayList<>();

    public ReportDocument() {
        preparationSections.add(new ReportSection(SectionType.PURPOSE));
        preparationSections.add(new ReportSection(SectionType.PRINCIPLE));
        preparationSections.add(new ReportSection(SectionType.METHOD));
        preparationSections.add(new ReportSection(SectionType.PRE_ASSIGNMENT));
        mainSections.add(new ReportSection(SectionType.RESULT));
        mainSections.add(new ReportSection(SectionType.DISCUSSION_SUMMARY));
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title == null ? "" : title; }
    public String getEnglishTitle() { return englishTitle; }
    public void setEnglishTitle(String englishTitle) { this.englishTitle = englishTitle == null ? "" : englishTitle; }
    public List<ReportSection> getPreparationSections() { return preparationSections; }
    public List<ReportSection> getMainSections() { return mainSections; }
    public List<Equipment> getEquipment() { return equipment; }
    public List<ReportFigure> getFigures() { return figures; }
    public List<ReportEquation> getEquations() { return equations; }
    public List<Reference> getReferences() { return references; }
}
