package main;

import controller.Controller;
import data.models.Model;
import data.services.datenservices.DatabaseCreationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import userinterface.View;
import util.statusmeldungen.StatusLog;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

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

        new Controller(view, model);

        final Pane defaultPanel = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/userinterface/haupt-view.fxml"))
        );
        view.setHauptPane(defaultPanel);

        final Scene scene = new Scene(loader.getRoot());
        stage.setTitle("Smart Home");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
