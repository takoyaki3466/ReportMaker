package org.takoyaki.reportmaker.model;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.UUID;
import org.takoyaki.reportmaker.i18n.I18n;

public final class ReportFigure implements Serializable {
    private final UUID id;
    private int number;
    private String name;
    private String imagePath;

    public ReportFigure(UUID id, int number, String name, Path imagePath) {
        this.id = id;
        this.number = number;
        this.name = name == null ? "" : name;
        this.imagePath = imagePath == null ? "" : imagePath.toString();
    }

    public UUID getId() { return id; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? "" : name; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath == null ? "" : imagePath; }

    @Override
    public String toString() {
        return I18n.text("display.figure", number, name);
    }
}
