package org.takoyaki.reportmaker.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.context.ContextAware;
import org.takoyaki.reportmaker.i18n.I18n;
import org.takoyaki.reportmaker.model.ReportEquation;
import org.takoyaki.reportmaker.util.DialogUtil;
import org.takoyaki.reportmaker.util.ExceptionUtil;
import org.takoyaki.reportmaker.view.ViewPaths;

import java.io.IOException;

public final class EquationController implements ContextAware {
    @FXML private ListView<ReportEquation> equationList;
    private ApplicationContext context;

    @Override public void setApplicationContext(ApplicationContext context) { this.context = context; refresh(); }
    @FXML private void add() { edit(null); }
    @FXML private void edit() { ReportEquation selected = equationList.getSelectionModel().getSelectedItem(); if (selected != null) edit(selected); }

    @FXML
    private void delete() {
        ReportEquation selected = equationList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        boolean referenced = context.getIntegrityService().isEquationReferenced(context.getDocument(), selected.getId());
        String message = I18n.text(referenced ? "confirm.deleteEquationReferenced" : "confirm.deleteEquation");
        if (DialogUtil.confirm(window(), I18n.text("dialog.deleteConfirmation"), message)) {
            context.getIntegrityService().removeEquationAndReferences(context.getDocument(), selected.getId());
            refresh();
        }
    }

    private void edit(ReportEquation original) {
        try {
            DialogUtil.<ReportEquation, EquationEditorDialogController>showEditor(
                    window(), I18n.text(original == null ? "dialog.addEquation" : "dialog.editEquation"),
                    ViewPaths.EQUATION_DIALOG,
                    controller -> controller.configure(context, original))
                    .ifPresent(result -> {
                        if (original == null) context.getDocument().getEquations().add(result);
                        else context.getDocument().getEquations().set(context.getDocument().getEquations().indexOf(original), result);
                        context.getNumberingService().renumberEquations(context.getDocument());
                        refresh();
                    });
        } catch (IOException exception) {
            DialogUtil.showError(window(), I18n.text("dialog.editorError"), ExceptionUtil.rootMessage(exception));
        }
    }

    private void refresh() { equationList.setItems(FXCollections.observableArrayList(context.getDocument().getEquations())); }
    private javafx.stage.Window window() { return equationList.getScene().getWindow(); }
}
