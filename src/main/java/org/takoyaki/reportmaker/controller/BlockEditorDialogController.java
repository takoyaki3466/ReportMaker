package org.takoyaki.reportmaker.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.i18n.I18n;
import org.takoyaki.reportmaker.model.BlockType;
import org.takoyaki.reportmaker.model.EquationBlock;
import org.takoyaki.reportmaker.model.FigureBlock;
import org.takoyaki.reportmaker.model.ReportBlock;
import org.takoyaki.reportmaker.model.ReportEquation;
import org.takoyaki.reportmaker.model.ReportFigure;
import org.takoyaki.reportmaker.model.SubsectionBlock;
import org.takoyaki.reportmaker.model.TextBlock;
import org.takoyaki.reportmaker.util.EditorDialogController;

import java.util.Optional;

public final class BlockEditorDialogController implements EditorDialogController<ReportBlock> {
    @FXML private ComboBox<BlockType> typeCombo;
    @FXML private Label textLabel;
    @FXML private TextArea textArea;
    @FXML private Label figureLabel;
    @FXML private ComboBox<ReportFigure> figureCombo;
    @FXML private Label equationLabel;
    @FXML private ComboBox<ReportEquation> equationCombo;

    @FXML
    private void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(BlockType.values()));
        typeCombo.valueProperty().addListener((observable, oldValue, value) -> updateFields());
    }

    public void configure(ApplicationContext context, ReportBlock original) {
        figureCombo.setItems(FXCollections.observableArrayList(context.getDocument().getFigures()));
        equationCombo.setItems(FXCollections.observableArrayList(context.getDocument().getEquations()));
        if (original == null) {
            typeCombo.setValue(BlockType.TEXT);
        } else {
            typeCombo.setValue(original.getType());
            typeCombo.setDisable(true);
            if (original instanceof TextBlock text) textArea.setText(text.getText());
            if (original instanceof SubsectionBlock subsection) textArea.setText(subsection.getTitle());
            if (original instanceof FigureBlock figure) selectFigure(figure);
            if (original instanceof EquationBlock equation) selectEquation(equation);
        }
        updateFields();
    }

    @Override
    public Optional<String> validateInput() {
        BlockType type = typeCombo.getValue();
        if ((type == BlockType.TEXT || type == BlockType.SUBSECTION) && textArea.getText().isBlank()) {
            return Optional.of(I18n.text(type == BlockType.TEXT ? "validation.text" : "validation.subsection"));
        }
        if (type == BlockType.FIGURE && figureCombo.getValue() == null) return Optional.of(I18n.text("validation.figureSelection"));
        if (type == BlockType.EQUATION && equationCombo.getValue() == null) return Optional.of(I18n.text("validation.equationSelection"));
        return Optional.empty();
    }

    @Override
    public ReportBlock buildResult() {
        return switch (typeCombo.getValue()) {
            case TEXT -> new TextBlock(textArea.getText());
            case SUBSECTION -> new SubsectionBlock(textArea.getText().strip());
            case FIGURE -> new FigureBlock(figureCombo.getValue().getId());
            case EQUATION -> new EquationBlock(equationCombo.getValue().getId());
        };
    }

    private void updateFields() {
        BlockType type = typeCombo.getValue();
        boolean text = type == BlockType.TEXT || type == BlockType.SUBSECTION;
        show(textLabel, text); show(textArea, text);
        show(figureLabel, type == BlockType.FIGURE); show(figureCombo, type == BlockType.FIGURE);
        show(equationLabel, type == BlockType.EQUATION); show(equationCombo, type == BlockType.EQUATION);
        textLabel.setText(I18n.text(type == BlockType.SUBSECTION ? "block.subsection" : "block.text"));
    }

    private void show(javafx.scene.Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    private void selectFigure(FigureBlock block) {
        figureCombo.getItems().stream().filter(item -> item.getId().equals(block.getFigureId()))
                .findFirst().ifPresent(figureCombo::setValue);
    }

    private void selectEquation(EquationBlock block) {
        equationCombo.getItems().stream().filter(item -> item.getId().equals(block.getEquationId()))
                .findFirst().ifPresent(equationCombo::setValue);
    }
}
