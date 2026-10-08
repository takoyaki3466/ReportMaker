package org.takoyaki.reportmaker.controller;

import org.takoyaki.reportmaker.model.ReportSection;

import java.util.List;

public final class PreparationReportController extends SectionEditorControllerBase {
    @Override
    protected List<ReportSection> sections() {
        return context.getDocument().getPreparationSections();
    }
}
