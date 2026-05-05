package com.smarthome;

import com.smarthome.controller.MainController;
import com.smarthome.model.repository.Database;
import com.smarthome.model.repository.GeraetRepository;
import com.smarthome.model.repository.RaumRepository;
import com.smarthome.model.service.RaumService;
import com.smarthome.util.StatusLog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class SmartHomeApplication extends Application {

    private static final String DB_URL = "./data/mydb";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws IOException {
        RaumService raumService = null;
        try {
            Database db = new Database(DB_URL, DB_USER, DB_PASSWORD);
            db.setupSchema();
            raumService = RaumService.setup(new RaumRepository(db), new GeraetRepository(db));
        } catch (SQLException e) {
            StatusLog.addError("Datenbankverbindung fehlgeschlagen", e);
        }

        FXMLLoader loader = new FXMLLoader(SmartHomeApplication.class.getResource("/org/example/ui/main-view.fxml"));
        loader.load();

        MainController mainController = loader.getController();
        mainController.setRaumService(raumService);

        Scene scene = new Scene(loader.getRoot());
        stage.setTitle("Smart Home");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
