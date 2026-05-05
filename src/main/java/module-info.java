module smarthome {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.commons.lang3;
    requires java.sql;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;

    opens com.smarthome to javafx.fxml;
    opens com.smarthome.controller to javafx.fxml;
    exports com.smarthome;
    exports com.smarthome.controller;
}