package org.takoyaki.reportmaker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.context.ContextAware;
import org.takoyaki.reportmaker.i18n.I18n;
import org.takoyaki.reportmaker.service.MarkdownFileService;
import org.takoyaki.reportmaker.util.DialogUtil;
import org.takoyaki.reportmaker.util.FileDialogUtil;
import org.takoyaki.reportmaker.util.ExceptionUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ImportExportController implements ContextAware {
    @FXML private VBox root;
    @FXML private Label statusLabel;
    private final MarkdownFileService markdownFileService = new MarkdownFileService();
    private ApplicationContext context;

    @Override public void setApplicationContext(ApplicationContext context) { this.context = context; }

    @FXML
    private void saveProject() {
        if (!validateTitle()) return;
        FileDialogUtil.chooseProjectToSave(window(), safeFileName("json")).ifPresent(path -> {
            try {
                context.getProjectFileService().save(path, context.getDocument());
                success(I18n.text("status.projectSaved", path));
            } catch (IOException exception) { error(I18n.text("error.projectSave"), exception); }
        });
    }

    @FXML
    private void openProject() {
        FileDialogUtil.chooseProjectToOpen(window()).ifPresent(path -> {
            try {
                context.setDocument(context.getProjectFileService().open(path));
                success(I18n.text("status.projectOpened", path));
            } catch (IOException exception) { error(I18n.text("error.projectOpen"), exception); }
        });
    }

    @FXML
    private void exportMarkdown() {
        if (!validateTitle()) return;
        FileDialogUtil.chooseMarkdownToSave(window(), safeFileName("md")).ifPresent(path -> {
            try {
                markdownFileService.export(path, context.getDocument(), context.getMarkdownRenderer());
                success(I18n.text("status.markdownExported", path));
            } catch (IOException exception) { error(I18n.text("error.markdownExport"), exception); }
        });
    }

    @FXML
    private void importMarkdown() {
        FileDialogUtil.chooseMarkdownToOpen(window()).ifPresent(path -> {
            try {
                String markdown = Files.readString(path, StandardCharsets.UTF_8);
                Path directory = path.toAbsolutePath().getParent();
                context.setDocument(context.getMarkdownParser().parse(markdown, directory));
                success(I18n.text("status.markdownImported", path));
            } catch (IOException | RuntimeException exception) { error(I18n.text("error.markdownImport"), exception); }
        });
    }

    private boolean validateTitle() {
        var error = context.getValidationService().required(
                context.getDocument().getTitle(), I18n.text("label.title"));
        error.ifPresent(message -> DialogUtil.showError(window(), I18n.text("dialog.inputError"), message));
        return error.isEmpty();
    }

    private String safeFileName(String extension) {
        String name = context.getDocument().getTitle().replaceAll("[\\\\/:*?\"<>|]", "_").strip();
        return (name.isBlank() ? "report" : name) + "." + extension;
    }

    private void success(String message) { statusLabel.setText(message); }
    private void error(String message, Exception exception) {
        DialogUtil.showError(window(), I18n.text("dialog.ioError"),
                message + "\n" + ExceptionUtil.rootMessage(exception));
    }
    private javafx.stage.Window window() { return root.getScene().getWindow(); }
}
