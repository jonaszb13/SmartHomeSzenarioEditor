package main;

import controller.Controller;
import data.models.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import userInterface.View;

import java.io.IOException;
import java.util.Objects;

public class SmartHomeApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        final FXMLLoader loader = new FXMLLoader(SmartHomeApplication.class.getResource("/userInterface/main-view.fxml"));
        loader.load();

        View view = loader.getController();
        //TODO hier müssen die ganzen Daten reingeladen werden
        //  -> vielleicht auch direkte Methoden im Modell,
        //     wodurch das keine Parameter mehr sein müssen
        Model model = new Model(null, null, null);
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
