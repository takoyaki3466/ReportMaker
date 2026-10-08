package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.ReportDocument;

import java.io.IOException;
import java.nio.file.Path;

public interface ProjectRepository {
    void save(Path projectPath, ReportDocument document) throws IOException;
    ReportDocument open(Path projectPath) throws IOException;
}
