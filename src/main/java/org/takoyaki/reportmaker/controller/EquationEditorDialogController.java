package org.takoyaki.reportmaker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.model.ReportEquation;
import org.takoyaki.reportmaker.util.EditorDialogController;

import java.util.Optional;
import java.util.UUID;

public final class EquationEditorDialogController implements EditorDialogController<ReportEquation> {
    @FXML private Label numberLabel;
    @FXML private TextField nameField;
    @FXML private TextArea latexArea;
    private ApplicationContext context;
    private UUID id;
    private int number;

    public void configure(ApplicationContext context, ReportEquation original) {
        this.context = context;
        id = original == null ? UUID.randomUUID() : original.getId();
        number = original == null ? context.getNumberingService().nextEquationNumber(context.getDocument()) : original.getNumber();
        numberLabel.setText(Integer.toString(number));
        if (original != null) { nameField.setText(original.getName()); latexArea.setText(original.getLatex()); }
    }

    @Override public Optional<String> validateInput() { return context.getValidationService().equation(latexArea.getText()); }
    @Override public ReportEquation buildResult() { return new ReportEquation(id, number, nameField.getText().strip(), latexArea.getText().strip()); }
}
