package org.takoyaki.reportmaker.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.context.ContextAware;
import org.takoyaki.reportmaker.i18n.I18n;
import org.takoyaki.reportmaker.model.EquationBlock;
import org.takoyaki.reportmaker.model.FigureBlock;
import org.takoyaki.reportmaker.model.ReportBlock;
import org.takoyaki.reportmaker.model.ReportEquation;
import org.takoyaki.reportmaker.model.ReportFigure;
import org.takoyaki.reportmaker.model.ReportSection;
import org.takoyaki.reportmaker.model.SubsectionBlock;
import org.takoyaki.reportmaker.model.TextBlock;
import org.takoyaki.reportmaker.util.DialogUtil;
import org.takoyaki.reportmaker.util.ExceptionUtil;
import org.takoyaki.reportmaker.util.ListOrderUtil;
import org.takoyaki.reportmaker.view.ViewPaths;

import java.io.IOException;
import java.util.List;

public abstract class SectionEditorControllerBase implements ContextAware {
    @FXML private ComboBox<ReportSection> sectionCombo;
    @FXML private ListView<ReportBlock> blockList;

    protected ApplicationContext context;

    protected abstract List<ReportSection> sections();

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
        sectionCombo.setItems(FXCollections.observableArrayList(sections()));
        sectionCombo.valueProperty().addListener((observable, oldValue, value) -> refreshBlocks());
        blockList.setCellFactory(ignored -> new ListCell<>() {
            @Override
            protected void updateItem(ReportBlock block, boolean empty) {
                super.updateItem(block, empty);
                setText(empty || block == null ? null : describe(block));
            }
        });
        if (!sectionCombo.getItems().isEmpty()) {
            sectionCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML private void addBlock() { openEditor(null); }

    @FXML private void editBlock() {
        ReportBlock selected = blockList.getSelectionModel().getSelectedItem();
        if (selected != null) openEditor(selected);
    }

    @FXML private void deleteBlock() {
        ReportSection section = sectionCombo.getValue();
        int index = blockList.getSelectionModel().getSelectedIndex();
        if (section != null && index >= 0 && DialogUtil.confirm(window(),
                I18n.text("dialog.deleteConfirmation"), I18n.text("confirm.deleteBlock"))) {
            section.getBlocks().remove(index);
            refreshBlocks();
        }
    }

    @FXML private void moveUp() { move(-1); }
    @FXML private void moveDown() { move(1); }

    private void openEditor(ReportBlock original) {
        ReportSection section = sectionCombo.getValue();
        if (section == null) return;
        try {
            DialogUtil.<ReportBlock, BlockEditorDialogController>showEditor(
                    window(), I18n.text(original == null ? "dialog.addBlock" : "dialog.editBlock"),
                    ViewPaths.BLOCK_DIALOG,
                    controller -> controller.configure(context, original))
                    .ifPresent(block -> {
                        if (original == null) {
                            section.getBlocks().add(block);
                        } else {
                            section.getBlocks().set(section.getBlocks().indexOf(original), block);
                        }
                        refreshBlocks();
                    });
        } catch (IOException exception) {
            DialogUtil.showError(window(), I18n.text("dialog.editorError"), ExceptionUtil.rootMessage(exception));
        }
    }

    private void move(int offset) {
        ReportSection section = sectionCombo.getValue();
        int from = blockList.getSelectionModel().getSelectedIndex();
        if (section == null) return;
        int to = ListOrderUtil.move(section.getBlocks(), from, offset);
        if (to == from) return;
        refreshBlocks();
        blockList.getSelectionModel().select(to);
    }

    private void refreshBlocks() {
        ReportSection section = sectionCombo.getValue();
        blockList.setItems(FXCollections.observableArrayList(
                section == null ? List.of() : section.getBlocks()));
    }

    private String describe(ReportBlock block) {
        if (block instanceof TextBlock text) {
            String oneLine = text.getText().replaceAll("\\s+", " ").strip();
            return I18n.text("display.textPrefix", oneLine.substring(0, Math.min(oneLine.length(), 70)));
        }
        if (block instanceof SubsectionBlock subsection) {
            return I18n.text("display.subsectionPrefix", subsection.getTitle());
        }
        if (block instanceof FigureBlock figureBlock) {
            return context.getDocument().getFigures().stream()
                    .filter(figure -> figure.getId().equals(figureBlock.getFigureId()))
                    .findFirst().map(ReportFigure::toString).orElse(I18n.text("display.missingFigure"));
        }
        EquationBlock equationBlock = (EquationBlock) block;
        return context.getDocument().getEquations().stream()
                .filter(equation -> equation.getId().equals(equationBlock.getEquationId()))
                .findFirst().map(ReportEquation::toString).orElse(I18n.text("display.missingEquation"));
    }

    private javafx.stage.Window window() {
        return blockList.getScene().getWindow();
    }
}
