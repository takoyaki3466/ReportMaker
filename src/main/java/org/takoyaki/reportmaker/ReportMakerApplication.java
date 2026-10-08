package org.takoyaki.reportmaker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.takoyaki.reportmaker.context.ApplicationContext;
import org.takoyaki.reportmaker.controller.MainController;
import org.takoyaki.reportmaker.i18n.I18n;
import org.takoyaki.reportmaker.view.ViewPaths;

import java.io.IOException;

public final class ReportMakerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        ApplicationContext context = new ApplicationContext();
        FXMLLoader loader = new FXMLLoader(
                ReportMakerApplication.class.getResource(ViewPaths.MAIN), I18n.bundle());
        Scene scene = new Scene(loader.load(), 1180, 780);
        MainController controller = loader.getController();
        controller.setApplicationContext(context);
        stage.setTitle(I18n.text("app.title"));
        stage.setMinWidth(900);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
