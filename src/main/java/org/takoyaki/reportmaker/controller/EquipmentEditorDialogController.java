package org.takoyaki.reportmaker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.takoyaki.reportmaker.model.Equipment;
import org.takoyaki.reportmaker.i18n.I18n;
import org.takoyaki.reportmaker.util.EditorDialogController;

import java.util.Optional;

public final class EquipmentEditorDialogController implements EditorDialogController<Equipment> {
    @FXML private TextField nameField;
    @FXML private TextField ratingField;
    @FXML private TextField classField;
    @FXML private TextField manufacturerField;
    @FXML private TextField modelField;
    @FXML private TextField serialField;
    @FXML private TextField assetField;

    public void setEquipment(Equipment equipment) {
        if (equipment == null) return;
        nameField.setText(equipment.getName()); ratingField.setText(equipment.getRating());
        classField.setText(equipment.getEquipmentClass()); manufacturerField.setText(equipment.getManufacturer());
        modelField.setText(equipment.getModel()); serialField.setText(equipment.getSerialNumber());
        assetField.setText(equipment.getAssetNumber());
    }

    @Override
    public Optional<String> validateInput() {
        return nameField.getText().isBlank()
                ? Optional.of(I18n.text("validation.equipmentName")) : Optional.empty();
    }

    @Override
    public Equipment buildResult() {
        Equipment equipment = new Equipment();
        equipment.setName(nameField.getText()); equipment.setRating(ratingField.getText());
        equipment.setEquipmentClass(classField.getText()); equipment.setManufacturer(manufacturerField.getText());
        equipment.setModel(modelField.getText()); equipment.setSerialNumber(serialField.getText());
        equipment.setAssetNumber(assetField.getText());
        return equipment;
    }
}
