package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.i18n.I18n;
import org.takoyaki.reportmaker.model.ReportDocument;
import org.takoyaki.reportmaker.model.ReportFigure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class ProjectFileService implements ProjectRepository {
    private final ProjectDocumentCodec codec;

    public ProjectFileService() {
        this(new ProjectJsonCodec());
    }

    public ProjectFileService(ProjectDocumentCodec codec) {
        this.codec = codec;
    }

    @Override
    public void save(Path projectPath, ReportDocument document) throws IOException {
        validateImages(document);
        Path absoluteProject = projectPath.toAbsolutePath();
        Path projectDirectory = absoluteProject.getParent();
        if (projectDirectory == null) {
            throw new IOException(I18n.text("error.invalidProjectPath"));
        }
        Path imageDirectory = projectDirectory.resolve(MarkdownConstants.IMAGES_DIRECTORY);
        Files.createDirectories(imageDirectory);
        for (ReportFigure figure : document.getFigures()) {
            Path source = Path.of(figure.getImagePath()).toAbsolutePath().normalize();
            Path destination = imageDirectory.resolve(imageFileName(figure)).toAbsolutePath().normalize();
            if (!source.equals(destination)) {
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        Files.writeString(absoluteProject, codec.encode(document), StandardCharsets.UTF_8);
    }

    @Override
    public ReportDocument open(Path projectPath) throws IOException {
        Path absoluteProject = projectPath.toAbsolutePath();
        try {
            String json = Files.readString(absoluteProject, StandardCharsets.UTF_8);
            ReportDocument document = codec.decode(json);
            restoreImagePaths(document, absoluteProject.getParent());
            return document;
        } catch (RuntimeException exception) {
            throw new IOException(I18n.text("error.projectInvalid"), exception);
        }
    }

    private void validateImages(ReportDocument document) throws IOException {
        for (ReportFigure figure : document.getFigures()) {
            Path source = Path.of(figure.getImagePath());
            if (!Files.isRegularFile(source)) {
                throw new IOException(I18n.text("error.missingImage", source));
            }
        }
    }

    private void restoreImagePaths(ReportDocument document, Path projectDirectory) throws IOException {
        if (projectDirectory == null) {
            throw new IOException(I18n.text("error.invalidProjectPath"));
        }
        for (ReportFigure figure : document.getFigures()) {
            Path relativePath = Path.of(figure.getImagePath());
            Path resolved = projectDirectory.resolve(relativePath).normalize();
            if (!resolved.startsWith(projectDirectory) || !Files.isRegularFile(resolved)) {
                throw new IOException(I18n.text("error.projectFigureMissing", figure.getNumber()));
            }
            figure.setImagePath(resolved.toString());
        }
    }

    private String imageFileName(ReportFigure figure) {
        String fileName = Path.of(figure.getImagePath()).getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        String extension = dot >= 0 ? fileName.substring(dot) : ".png";
        return figure.getId() + extension;
    }
}
