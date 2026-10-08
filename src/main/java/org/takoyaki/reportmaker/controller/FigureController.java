package org.takoyaki.reportmaker.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.context.ContextAware;
import org.takoyaki.reportmaker.i18n.I18n;
import org.takoyaki.reportmaker.model.ReportFigure;
import org.takoyaki.reportmaker.util.DialogUtil;
import org.takoyaki.reportmaker.util.ExceptionUtil;
import org.takoyaki.reportmaker.view.ViewPaths;

import java.io.IOException;
import java.util.UUID;

public final class FigureController implements ContextAware {
    @FXML private ListView<ReportFigure> figureList;
    private ApplicationContext context;

    @Override public void setApplicationContext(ApplicationContext context) { this.context = context; refresh(); }
    @FXML private void add() { edit(null); }
    @FXML private void edit() { ReportFigure selected = figureList.getSelectionModel().getSelectedItem(); if (selected != null) edit(selected); }

    @FXML
    private void delete() {
        ReportFigure selected = figureList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        boolean referenced = context.getIntegrityService().isFigureReferenced(context.getDocument(), selected.getId());
        String message = I18n.text(referenced ? "confirm.deleteFigureReferenced" : "confirm.deleteFigure");
        if (DialogUtil.confirm(window(), I18n.text("dialog.deleteConfirmation"), message)) {
            context.getIntegrityService().removeFigureAndReferences(context.getDocument(), selected.getId());
            refresh();
        }
    }

    private void edit(ReportFigure original) {
        try {
            DialogUtil.<ReportFigure, FigureEditorDialogController>showEditor(
                    window(), I18n.text(original == null ? "dialog.addFigure" : "dialog.editFigure"),
                    ViewPaths.FIGURE_DIALOG,
                    controller -> controller.configure(context, original))
                    .ifPresent(result -> {
                        if (original == null) context.getDocument().getFigures().add(result);
                        else context.getDocument().getFigures().set(context.getDocument().getFigures().indexOf(original), result);
                        context.getNumberingService().renumberFigures(context.getDocument());
                        refresh();
                    });
        } catch (IOException exception) {
            DialogUtil.showError(window(), I18n.text("dialog.editorError"), ExceptionUtil.rootMessage(exception));
        }
    }

    private void refresh() { figureList.setItems(FXCollections.observableArrayList(context.getDocument().getFigures())); }
    private javafx.stage.Window window() { return figureList.getScene().getWindow(); }
}
