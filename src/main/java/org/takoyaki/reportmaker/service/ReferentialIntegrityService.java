package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.EquationBlock;
import org.takoyaki.reportmaker.model.FigureBlock;
import org.takoyaki.reportmaker.model.ReportDocument;
import org.takoyaki.reportmaker.model.ReportSection;

import java.util.UUID;
import java.util.stream.Stream;

public final class ReferentialIntegrityService implements ReferenceIntegrityManager {
    private final NumberingPolicy numberingService;

    public ReferentialIntegrityService(NumberingPolicy numberingService) {
        this.numberingService = numberingService;
    }

    public boolean isFigureReferenced(ReportDocument document, UUID figureId) {
        return sections(document).flatMap(section -> section.getBlocks().stream())
                .anyMatch(block -> block instanceof FigureBlock figure && figure.getFigureId().equals(figureId));
    }

    public boolean isEquationReferenced(ReportDocument document, UUID equationId) {
        return sections(document).flatMap(section -> section.getBlocks().stream())
                .anyMatch(block -> block instanceof EquationBlock equation && equation.getEquationId().equals(equationId));
    }

    public void removeFigureAndReferences(ReportDocument document, UUID figureId) {
        sections(document).forEach(section -> section.getBlocks().removeIf(
                block -> block instanceof FigureBlock figure && figure.getFigureId().equals(figureId)));
        document.getFigures().removeIf(figure -> figure.getId().equals(figureId));
        numberingService.renumberFigures(document);
    }

    public void removeEquationAndReferences(ReportDocument document, UUID equationId) {
        sections(document).forEach(section -> section.getBlocks().removeIf(
                block -> block instanceof EquationBlock equation && equation.getEquationId().equals(equationId)));
        document.getEquations().removeIf(equation -> equation.getId().equals(equationId));
        numberingService.renumberEquations(document);
    }

    private Stream<ReportSection> sections(ReportDocument document) {
        return Stream.concat(document.getPreparationSections().stream(), document.getMainSections().stream());
    }
}
