package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.ReportDocument;

import java.util.UUID;

public interface ReferenceIntegrityManager {
    boolean isFigureReferenced(ReportDocument document, UUID figureId);
    boolean isEquationReferenced(ReportDocument document, UUID equationId);
    void removeFigureAndReferences(ReportDocument document, UUID figureId);
    void removeEquationAndReferences(ReportDocument document, UUID equationId);
}
