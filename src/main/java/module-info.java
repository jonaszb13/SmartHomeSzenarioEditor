module org.example.ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires jakarta.inject;
    requires org.apache.commons.lang3;
    requires java.sql;
    requires java.desktop;
    requires javafx.graphics;

    opens org.example to javafx.fxml;
    opens org.example.ui to javafx.fxml;
    opens userInterface.views to javafx.fxml;
    exports org.example;
    exports userInterface.views;
    exports controller;
}