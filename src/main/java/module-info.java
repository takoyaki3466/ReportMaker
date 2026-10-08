module org.takoyaki.reportmaker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;


    opens org.takoyaki.reportmaker to javafx.fxml;
    exports org.takoyaki.reportmaker;
    exports org.takoyaki.reportmaker.model;
    opens org.takoyaki.reportmaker.model to javafx.fxml;
    exports org.takoyaki.reportmaker.controller;
    opens org.takoyaki.reportmaker.controller to javafx.fxml;
    exports org.takoyaki.reportmaker.service;
    opens org.takoyaki.reportmaker.service to javafx.fxml;
}
