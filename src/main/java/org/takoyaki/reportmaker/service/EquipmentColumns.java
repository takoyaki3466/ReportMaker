package org.takoyaki.reportmaker.service;

import java.util.List;
import org.takoyaki.reportmaker.i18n.I18n;

public final class EquipmentColumns {
    private EquipmentColumns() {
    }

    public static List<String> headers() {
        return List.of(I18n.text("equipment.name"), I18n.text("equipment.rating"),
                I18n.text("equipment.class"), I18n.text("equipment.manufacturer"),
                I18n.text("equipment.model"), I18n.text("equipment.serialNumber"),
                I18n.text("equipment.assetNumber"));
    }
}
