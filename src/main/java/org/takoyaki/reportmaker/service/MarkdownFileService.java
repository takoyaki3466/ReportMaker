package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.ReportDocument;
import org.takoyaki.reportmaker.model.ReportFigure;
import org.takoyaki.reportmaker.i18n.I18n;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MarkdownFileService {
    public void export(Path markdownPath, ReportDocument document, MarkdownGenerator renderer) throws IOException {
        for (ReportFigure figure : document.getFigures()) {
            Path source = Path.of(figure.getImagePath());
            if (!Files.isRegularFile(source)) {
                throw new IOException(I18n.text("error.missingImage", source));
            }
        }
        Files.writeString(markdownPath, renderer.render(document), StandardCharsets.UTF_8);
        if (document.getFigures().isEmpty()) {
            return;
        }
        Path imageDirectory = markdownPath.toAbsolutePath().getParent().resolve(MarkdownConstants.IMAGES_DIRECTORY);
        Files.createDirectories(imageDirectory);
        for (ReportFigure figure : document.getFigures()) {
            Path source = Path.of(figure.getImagePath());
            Files.copy(source, imageDirectory.resolve(figureFileName(figure)),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String figureFileName(ReportFigure figure) {
        String fileName = Path.of(figure.getImagePath()).getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        String extension = dot >= 0 ? fileName.substring(dot).toLowerCase() : ".png";
        return "figure-%03d%s".formatted(figure.getNumber(), extension);
    }
}
