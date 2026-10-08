package org.takoyaki.reportmaker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.model.ReportFigure;
import org.takoyaki.reportmaker.util.EditorDialogController;
import org.takoyaki.reportmaker.util.FileDialogUtil;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public final class FigureEditorDialogController implements EditorDialogController<ReportFigure> {
    @FXML private Label numberLabel;
    @FXML private TextField nameField;
    @FXML private TextField pathField;
    private ApplicationContext context;
    private UUID id;
    private int number;

    public void configure(ApplicationContext context, ReportFigure original) {
        this.context = context;
        id = original == null ? UUID.randomUUID() : original.getId();
        number = original == null ? context.getNumberingService().nextFigureNumber(context.getDocument()) : original.getNumber();
        numberLabel.setText(Integer.toString(number));
        if (original != null) { nameField.setText(original.getName()); pathField.setText(original.getImagePath()); }
    }

    @FXML
    private void browse() {
        FileDialogUtil.chooseImage(pathField.getScene().getWindow()).ifPresent(path -> pathField.setText(path.toString()));
    }

    @Override public Optional<String> validateInput() {
        Path path = pathField.getText().isBlank() ? null : Path.of(pathField.getText());
        return context.getValidationService().image(nameField.getText(), path);
    }

    @Override public ReportFigure buildResult() {
        return new ReportFigure(id, number, nameField.getText().strip(), Path.of(pathField.getText()));
    }
}
