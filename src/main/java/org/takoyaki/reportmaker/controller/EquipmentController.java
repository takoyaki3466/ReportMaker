package org.takoyaki.reportmaker.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.context.ContextAware;
import org.takoyaki.reportmaker.i18n.I18n;
import org.takoyaki.reportmaker.model.Equipment;
import org.takoyaki.reportmaker.util.DialogUtil;
import org.takoyaki.reportmaker.util.ExceptionUtil;
import org.takoyaki.reportmaker.util.ListOrderUtil;
import org.takoyaki.reportmaker.view.ViewPaths;

import java.io.IOException;
import java.util.function.Function;

public final class EquipmentController implements ContextAware {
    @FXML private TableView<Equipment> equipmentTable;
    @FXML private TableColumn<Equipment, String> nameColumn;
    @FXML private TableColumn<Equipment, String> ratingColumn;
    @FXML private TableColumn<Equipment, String> classColumn;
    @FXML private TableColumn<Equipment, String> manufacturerColumn;
    @FXML private TableColumn<Equipment, String> modelColumn;
    @FXML private TableColumn<Equipment, String> serialColumn;
    @FXML private TableColumn<Equipment, String> assetColumn;
    private ApplicationContext context;

    @FXML
    private void initialize() {
        equipmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        bind(nameColumn, Equipment::getName); bind(ratingColumn, Equipment::getRating);
        bind(classColumn, Equipment::getEquipmentClass); bind(manufacturerColumn, Equipment::getManufacturer);
        bind(modelColumn, Equipment::getModel); bind(serialColumn, Equipment::getSerialNumber);
        bind(assetColumn, Equipment::getAssetNumber);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
        refresh();
    }

    @FXML private void add() { edit(null); }

    @FXML private void edit() {
        Equipment selected = equipmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) edit(selected);
    }

    @FXML private void delete() {
        Equipment selected = equipmentTable.getSelectionModel().getSelectedItem();
        if (selected != null && DialogUtil.confirm(window(), I18n.text("dialog.deleteConfirmation"),
                I18n.text("confirm.deleteEquipment"))) {
            context.getDocument().getEquipment().remove(selected);
            refresh();
        }
    }

    @FXML private void moveUp() { move(-1); }
    @FXML private void moveDown() { move(1); }

    private void edit(Equipment original) {
        try {
            DialogUtil.<Equipment, EquipmentEditorDialogController>showEditor(
                    window(), I18n.text(original == null ? "dialog.addEquipment" : "dialog.editEquipment"),
                    ViewPaths.EQUIPMENT_DIALOG, controller -> controller.setEquipment(original))
                    .ifPresent(result -> {
                        if (original == null) context.getDocument().getEquipment().add(result);
                        else context.getDocument().getEquipment().set(
                                context.getDocument().getEquipment().indexOf(original), result);
                        refresh();
                    });
        } catch (IOException exception) {
            DialogUtil.showError(window(), I18n.text("dialog.editorError"), ExceptionUtil.rootMessage(exception));
        }
    }

    private void move(int offset) {
        int from = equipmentTable.getSelectionModel().getSelectedIndex();
        int to = ListOrderUtil.move(context.getDocument().getEquipment(), from, offset);
        if (to == from) return;
        refresh();
        equipmentTable.getSelectionModel().select(to);
    }

    private void refresh() {
        equipmentTable.setItems(FXCollections.observableArrayList(context.getDocument().getEquipment()));
    }

    private void bind(TableColumn<Equipment, String> column, Function<Equipment, String> getter) {
        column.setCellValueFactory(cell -> new SimpleStringProperty(getter.apply(cell.getValue())));
    }

    private javafx.stage.Window window() { return equipmentTable.getScene().getWindow(); }
}
