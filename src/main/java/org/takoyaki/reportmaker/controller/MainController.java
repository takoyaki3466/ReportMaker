package org.takoyaki.reportmaker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.context.ContextAware;
import org.takoyaki.reportmaker.i18n.I18n;
import org.takoyaki.reportmaker.util.DialogUtil;
import org.takoyaki.reportmaker.util.ExceptionUtil;
import org.takoyaki.reportmaker.view.ViewManager;
import org.takoyaki.reportmaker.view.ViewPaths;

import java.io.IOException;

public final class MainController implements ContextAware {
    @FXML private TextField titleField;
    @FXML private TextField englishTitleField;
    @FXML private StackPane contentHost;

    private ApplicationContext context;
    private ViewManager viewManager;
    private boolean refreshing;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
        viewManager = new ViewManager(context, contentHost);
        titleField.textProperty().addListener((observable, oldValue, value) -> {
            if (!refreshing) context.getDocument().setTitle(value);
        });
        englishTitleField.textProperty().addListener((observable, oldValue, value) -> {
            if (!refreshing) context.getDocument().setEnglishTitle(value);
        });
        context.documentProperty().addListener((observable, oldValue, value) -> refreshHeader());
        refreshHeader();
        show(ViewPaths.PREPARATION);
    }

    @FXML private void showPreparation() { show(ViewPaths.PREPARATION); }
    @FXML private void showMainReport() { show(ViewPaths.MAIN_REPORT); }
    @FXML private void showEquipment() { show(ViewPaths.EQUIPMENT); }
    @FXML private void showFigures() { show(ViewPaths.FIGURE); }
    @FXML private void showEquations() { show(ViewPaths.EQUATION); }
    @FXML private void showReferences() { show(ViewPaths.REFERENCE); }
    @FXML private void showPreview() { show(ViewPaths.PREVIEW); }
    @FXML private void showImportExport() { show(ViewPaths.IMPORT_EXPORT); }

    private void refreshHeader() {
        refreshing = true;
        titleField.setText(context.getDocument().getTitle());
        englishTitleField.setText(context.getDocument().getEnglishTitle());
        refreshing = false;
    }

    private void show(String path) {
        try {
            viewManager.show(path);
        } catch (IOException exception) {
            DialogUtil.showError(contentHost.getScene().getWindow(), I18n.text("dialog.viewError"),
                    I18n.text("dialog.loadError", ExceptionUtil.rootMessage(exception)));
        }
    }
}
