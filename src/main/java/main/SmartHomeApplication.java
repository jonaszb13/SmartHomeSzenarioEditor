package main;

import controller.Controller;
import data.models.Model;
import data.services.datenServices.DatabaseCreationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import userInterface.View;
import util.statusmeldungen.StatusLog;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class SmartHomeApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        try {
            DatabaseCreationService.createDatabase();
        } catch (SQLException e) {
            StatusLog.addError("Fehler bei Datenbankerstellung", e);
        }


        final FXMLLoader loader = new FXMLLoader(SmartHomeApplication.class.getResource("/userInterface/main-view.fxml"));
        loader.load();

        View view = loader.getController();

        Model model = Model.getInstance();
        view.updateTreeModel(model.getRaeume(), model.getGeraete(), model.getSzenarien());

        new Controller(view, model);

        Pane defaultPanel = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/userInterface/haupt-view.fxml"))
        );
        view.getHauptPane().getChildren().setAll(defaultPanel);

        Scene scene = new Scene(loader.getRoot());
        stage.setTitle("Smart Home");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
