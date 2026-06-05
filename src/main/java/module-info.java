module src.main {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires jakarta.inject;
    requires org.apache.commons.lang3;
    requires java.desktop;
    requires javafx.graphics;
    requires java.sql.rowset;

    opens main to javafx.fxml;
    opens userinterface to javafx.fxml;
    opens controller to javafx.fxml;
    exports userinterface;
    exports controller;
    exports main;
    exports data.models;
    exports data.models.fachobjekte;
    exports util.statusmeldungen;
    exports data.models.fachobjekte.geraetearten;
    exports util.customexceptions;
    exports util;
}