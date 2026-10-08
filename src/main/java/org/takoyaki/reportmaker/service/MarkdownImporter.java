package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.ReportDocument;

import java.nio.file.Path;

public interface MarkdownImporter {
    ReportDocument parse(String markdown, Path sourceDirectory);
}
