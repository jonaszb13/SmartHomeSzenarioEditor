package main;

import controller.Controller;
import data.models.Model;
import data.services.datenservices.DatabaseCreationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import userinterface.View;
import util.statusmeldungen.StatusLog;

import java.io.IOException;
import java.sql.SQLException;

public class SmartHomeApplication extends Application {

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws IOException {
        try {
            DatabaseCreationService.createDatabase();
        } catch (SQLException e) {
            StatusLog.addError("Fehler bei Datenbankerstellung", e);
        }


        final FXMLLoader loader = new FXMLLoader(SmartHomeApplication.class.getResource("/userinterface/main-view.fxml"));
        loader.load();

        final View view = loader.getController();

        final Model model = Model.getInstance();
        view.updateTreeModel(model.getRaumMap(), model.getGeraete(), model.getSzenarioMap());

        Controller controller = new Controller(view, model);
        controller.zeigeStandardansicht();

        final Scene scene = new Scene(loader.getRoot());
        stage.setTitle("Smart Home");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
