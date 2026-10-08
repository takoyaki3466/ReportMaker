package org.takoyaki.reportmaker.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.takoyaki.reportmaker.ReportMakerApplication;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.context.ContextAware;
import org.takoyaki.reportmaker.i18n.I18n;
import org.takoyaki.reportmaker.util.StyleUtil;

import java.io.IOException;

public final class ViewManager {
    private final ApplicationContext context;
    private final StackPane host;

    public ViewManager(ApplicationContext context, StackPane host) {
        this.context = context;
        this.host = host;
    }

    public void show(String resourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ReportMakerApplication.class.getResource(resourcePath), I18n.bundle());
        Node view = loader.load();
        StyleUtil.applyModernStyle(view);
        Object controller = loader.getController();
        if (controller instanceof ContextAware contextAware) {
            contextAware.setApplicationContext(context);
        }
        host.getChildren().setAll(view);
    }
}
