package org.takoyaki.reportmaker.model;

import java.io.Serializable;

public final class Equipment implements Serializable {
    private String name = "";
    private String rating = "";
    private String equipmentClass = "";
    private String manufacturer = "";
    private String model = "";
    private String serialNumber = "";
    private String assetNumber = "";

    public String getName() { return name; }
    public void setName(String name) { this.name = normalized(name); }
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = normalized(rating); }
    public String getEquipmentClass() { return equipmentClass; }
    public void setEquipmentClass(String equipmentClass) { this.equipmentClass = normalized(equipmentClass); }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = normalized(manufacturer); }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = normalized(model); }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = normalized(serialNumber); }
    public String getAssetNumber() { return assetNumber; }
    public void setAssetNumber(String assetNumber) { this.assetNumber = normalized(assetNumber); }

    private String normalized(String value) {
        return value == null ? "" : value;
    }
}
