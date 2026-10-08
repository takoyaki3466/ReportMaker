package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.ReportDocument;
import org.takoyaki.reportmaker.model.ReportEquation;
import org.takoyaki.reportmaker.model.ReportFigure;
import org.takoyaki.reportmaker.model.ReportSection;
import org.takoyaki.reportmaker.model.SectionType;

public final class NumberingService implements NumberingPolicy {
    public int nextFigureNumber(ReportDocument document) {
        return document.getFigures().stream().mapToInt(ReportFigure::getNumber).max().orElse(0) + 1;
    }

    public int nextEquationNumber(ReportDocument document) {
        return document.getEquations().stream().mapToInt(ReportEquation::getNumber).max().orElse(0) + 1;
    }

    public String subsectionNumber(ReportSection section, int subsectionIndex) {
        int major = section.getType().getNumber();
        return major > 0 ? major + "." + subsectionIndex : Integer.toString(subsectionIndex);
    }

    public void renumberFigures(ReportDocument document) {
        for (int index = 0; index < document.getFigures().size(); index++) {
            document.getFigures().get(index).setNumber(index + 1);
        }
    }

    public void renumberEquations(ReportDocument document) {
        for (int index = 0; index < document.getEquations().size(); index++) {
            document.getEquations().get(index).setNumber(index + 1);
        }
    }

    public SectionType typeForTitle(String title) {
        return SectionType.fromTitle(title.strip());
    }
}
