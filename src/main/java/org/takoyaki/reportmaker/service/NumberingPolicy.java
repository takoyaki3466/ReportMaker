package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.ReportDocument;
import org.takoyaki.reportmaker.model.ReportSection;
import org.takoyaki.reportmaker.model.SectionType;

public interface NumberingPolicy {
    int nextFigureNumber(ReportDocument document);
    int nextEquationNumber(ReportDocument document);
    String subsectionNumber(ReportSection section, int subsectionIndex);
    void renumberFigures(ReportDocument document);
    void renumberEquations(ReportDocument document);
    SectionType typeForTitle(String title);
}
