package org.takoyaki.reportmaker.model;

import java.io.Serializable;
import java.util.UUID;
import org.takoyaki.reportmaker.i18n.I18n;

public final class ReportEquation implements Serializable {
    private final UUID id;
    private int number;
    private String name;
    private String latex;

    public ReportEquation(UUID id, int number, String name, String latex) {
        this.id = id;
        this.number = number;
        this.name = name == null ? "" : name;
        this.latex = latex == null ? "" : latex;
    }

    public UUID getId() { return id; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? "" : name; }
    public String getLatex() { return latex; }
    public void setLatex(String latex) { this.latex = latex == null ? "" : latex; }

    @Override
    public String toString() {
        return I18n.text("display.equation", number, name);
    }
}
