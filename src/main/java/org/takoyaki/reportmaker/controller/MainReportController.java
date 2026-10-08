package org.takoyaki.reportmaker.controller;

import org.takoyaki.reportmaker.model.ReportSection;

import java.util.List;

public final class MainReportController extends SectionEditorControllerBase {
    @Override
    protected List<ReportSection> sections() {
        return context.getDocument().getMainSections();
    }
}
