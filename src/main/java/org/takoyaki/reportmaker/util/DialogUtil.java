package org.takoyaki.reportmaker.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Window;
import org.takoyaki.reportmaker.ReportMakerApplication;
import org.takoyaki.reportmaker.i18n.I18n;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

public final class DialogUtil {
    private DialogUtil() {
    }

    public static <T, C extends EditorDialogController<T>> Optional<T> showEditor(
            Window owner, String title, String fxml, Consumer<C> initializer) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ReportMakerApplication.class.getResource(fxml), I18n.bundle());
        Node content = loader.load();
        StyleUtil.applyModernStyle(content);
        C controller = (C) loader.getController();
        initializer.accept(controller);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(title);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            Optional<String> error = controller.validateInput();
            if (error.isPresent()) {
                showError(owner, I18n.text("dialog.inputError"), error.get());
                event.consume();
            }
        });
        return dialog.showAndWait().filter(ButtonType.OK::equals).map(ignored -> controller.buildResult());
    }

    public static void showError(Window owner, String title, String message) {
        showAlert(owner, Alert.AlertType.ERROR, title, message);
    }

    public static void showInformation(Window owner, String title, String message) {
        showAlert(owner, Alert.AlertType.INFORMATION, title, message);
    }

    public static boolean confirm(Window owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private static void showAlert(Window owner, Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
