package org.takoyaki.reportmaker.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.takoyaki.reportmaker.i18n.I18n;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public final class FileDialogUtil {
    private FileDialogUtil() {
    }

    public static Optional<Path> chooseImage(Window owner) {
        FileChooser chooser = chooser(I18n.text("file.chooseImage"), I18n.text("file.image"),
                "*.png", "*.jpg", "*.jpeg", "*.gif");
        return Optional.ofNullable(chooser.showOpenDialog(owner)).map(File::toPath);
    }

    public static Optional<Path> chooseProjectToOpen(Window owner) {
        return open(owner, chooser(I18n.text("file.openProject"), I18n.text("file.project"), "*.json"));
    }

    public static Optional<Path> chooseProjectToSave(Window owner, String fileName) {
        FileChooser chooser = chooser(I18n.text("file.saveProject"), I18n.text("file.project"), "*.json");
        chooser.setInitialFileName(fileName);
        return save(owner, chooser);
    }

    public static Optional<Path> chooseMarkdownToOpen(Window owner) {
        return open(owner, chooser(I18n.text("file.openMarkdown"), I18n.text("file.markdown"), "*.md", "*.markdown"));
    }

    public static Optional<Path> chooseMarkdownToSave(Window owner, String fileName) {
        FileChooser chooser = chooser(I18n.text("file.saveMarkdown"), I18n.text("file.markdown"), "*.md");
        chooser.setInitialFileName(fileName);
        return save(owner, chooser);
    }

    private static FileChooser chooser(String title, String description, String... patterns) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, patterns));
        return chooser;
    }

    private static Optional<Path> open(Window owner, FileChooser chooser) {
        return Optional.ofNullable(chooser.showOpenDialog(owner)).map(File::toPath);
    }

    private static Optional<Path> save(Window owner, FileChooser chooser) {
        return Optional.ofNullable(chooser.showSaveDialog(owner)).map(File::toPath);
    }
}
