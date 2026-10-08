package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.ReportDocument;

public interface MarkdownGenerator {
    String render(ReportDocument document);
}
