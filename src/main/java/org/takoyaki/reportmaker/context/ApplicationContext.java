package org.takoyaki.reportmaker.context;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.takoyaki.reportmaker.model.ReportDocument;
import org.takoyaki.reportmaker.service.MarkdownParser;
import org.takoyaki.reportmaker.service.MarkdownRenderer;
import org.takoyaki.reportmaker.service.MarkdownGenerator;
import org.takoyaki.reportmaker.service.MarkdownImporter;
import org.takoyaki.reportmaker.service.NumberingService;
import org.takoyaki.reportmaker.service.NumberingPolicy;
import org.takoyaki.reportmaker.service.ProjectFileService;
import org.takoyaki.reportmaker.service.ProjectRepository;
import org.takoyaki.reportmaker.service.ReferentialIntegrityService;
import org.takoyaki.reportmaker.service.ReferenceIntegrityManager;
import org.takoyaki.reportmaker.service.ValidationService;
import org.takoyaki.reportmaker.service.DocumentValidator;

public final class ApplicationContext {
    private final ObjectProperty<ReportDocument> document = new SimpleObjectProperty<>(new ReportDocument());
    private final NumberingPolicy numberingService = new NumberingService();
    private final MarkdownGenerator markdownRenderer = new MarkdownRenderer(numberingService);
    private final MarkdownImporter markdownParser = new MarkdownParser(numberingService);
    private final ProjectRepository projectFileService = new ProjectFileService();
    private final ReferenceIntegrityManager integrityService = new ReferentialIntegrityService(numberingService);
    private final DocumentValidator validationService = new ValidationService();

    public ReportDocument getDocument() { return document.get(); }
    public void setDocument(ReportDocument document) { this.document.set(document); }
    public ObjectProperty<ReportDocument> documentProperty() { return document; }
    public NumberingPolicy getNumberingService() { return numberingService; }
    public MarkdownGenerator getMarkdownRenderer() { return markdownRenderer; }
    public MarkdownImporter getMarkdownParser() { return markdownParser; }
    public ProjectRepository getProjectFileService() { return projectFileService; }
    public ReferenceIntegrityManager getIntegrityService() { return integrityService; }
    public DocumentValidator getValidationService() { return validationService; }
}
