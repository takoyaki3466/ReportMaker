package org.takoyaki.reportmaker.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.context.ContextAware;
import org.takoyaki.reportmaker.i18n.I18n;
import org.takoyaki.reportmaker.model.Reference;
import org.takoyaki.reportmaker.util.DialogUtil;
import org.takoyaki.reportmaker.util.ExceptionUtil;
import org.takoyaki.reportmaker.util.ListOrderUtil;
import org.takoyaki.reportmaker.view.ViewPaths;

import java.io.IOException;

public final class ReferenceController implements ContextAware {
    @FXML private ListView<Reference> referenceList;
    private ApplicationContext context;

    @FXML
    private void initialize() {
        referenceList.setCellFactory(ignored -> new ListCell<>() {
            @Override protected void updateItem(Reference reference, boolean empty) {
                super.updateItem(reference, empty);
                setText(empty || reference == null ? null
                        : I18n.text("display.reference", reference.getType(), reference.getDisplayText()));
            }
        });
    }

    @Override public void setApplicationContext(ApplicationContext context) { this.context = context; refresh(); }
    @FXML private void add() { edit(null); }
    @FXML private void edit() { Reference selected = referenceList.getSelectionModel().getSelectedItem(); if (selected != null) edit(selected); }

    @FXML
    private void delete() {
        Reference selected = referenceList.getSelectionModel().getSelectedItem();
        if (selected != null && DialogUtil.confirm(window(), I18n.text("dialog.deleteConfirmation"),
                I18n.text("confirm.deleteReference"))) {
            context.getDocument().getReferences().remove(selected); refresh();
        }
    }

    @FXML private void moveUp() { move(-1); }
    @FXML private void moveDown() { move(1); }

    private void edit(Reference original) {
        try {
            DialogUtil.<Reference, ReferenceEditorDialogController>showEditor(
                    window(), I18n.text(original == null ? "dialog.addReference" : "dialog.editReference"),
                    ViewPaths.REFERENCE_DIALOG,
                    controller -> controller.configure(context, original))
                    .ifPresent(result -> {
                        if (original == null) context.getDocument().getReferences().add(result);
                        else context.getDocument().getReferences().set(context.getDocument().getReferences().indexOf(original), result);
                        refresh();
                    });
        } catch (IOException exception) {
            DialogUtil.showError(window(), I18n.text("dialog.editorError"), ExceptionUtil.rootMessage(exception));
        }
    }

    private void move(int offset) {
        int from = referenceList.getSelectionModel().getSelectedIndex();
        int to = ListOrderUtil.move(context.getDocument().getReferences(), from, offset);
        if (to == from) return;
        refresh();
        referenceList.getSelectionModel().select(to);
    }

    private void refresh() { referenceList.setItems(FXCollections.observableArrayList(context.getDocument().getReferences())); }
    private javafx.stage.Window window() { return referenceList.getScene().getWindow(); }
}
