package org.takoyaki.reportmaker.util;

import javafx.scene.Node;
import javafx.scene.Parent;
import org.takoyaki.reportmaker.ReportMakerApplication;

import java.net.URL;
import java.util.Objects;

public final class StyleUtil {
    private static final String MODERN_STYLE_PATH = "/org/takoyaki/reportmaker/modern-style.css";

    private StyleUtil() {
    }

    public static void applyModernStyle(Node node) {
        if (!(node instanceof Parent parent)) {
            return;
        }
        URL resource = Objects.requireNonNull(
                ReportMakerApplication.class.getResource(MODERN_STYLE_PATH), MODERN_STYLE_PATH);
        String stylesheet = resource.toExternalForm();
        if (!parent.getStylesheets().contains(stylesheet)) {
            parent.getStylesheets().add(stylesheet);
        }
    }
}
