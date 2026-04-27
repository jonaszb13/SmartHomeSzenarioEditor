package org.example;

import controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import userInterface.views.View;

import java.io.IOException;
import java.util.Objects;

public class SmartHomeApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(SmartHomeApplication.class.getResource("/org/example/ui/main-view.fxml"));
        loader.load();

        View view = loader.getController();
        new Controller(view);

        Pane defaultPanel = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/org/example/ui/haupt-view.fxml"))
        );
        view.getHauptPane().getChildren().setAll(defaultPanel);

        //TODO: so kann bspw. der Text im Statusfeld gesetzt werden
        view.getStatusPanel().setText("testlsjatklakltlk\nteksltkjatklatlaklt\ndsajfkasdjfljaskflka");

        Scene scene = new Scene(loader.getRoot());
        stage.setTitle("Smart Home");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
