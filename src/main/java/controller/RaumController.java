package controller;

import data.models.fachobjekte.DAO;
import data.models.fachobjekte.Raum;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.stream.Collectors;

public class RaumController {

    @FXML
    private Label raumNameLabel;
    @FXML
    private TextField raumNameField;
    @FXML
    private ListView<String> geraeteListView;

    private Runnable onSchliessen;
    private static RaumController instance;

    public static RaumController getInstance() {
        if (instance == null) {
            instance = new RaumController();
            instance.raumNameLabel = new Label();
            instance.raumNameField = new TextField();
            instance.geraeteListView = new ListView<>();
        }
        return instance;
    }

    public void setRaum(final Raum raum) {
        raumNameLabel.setText(raum.getName());
        raumNameField.setText(raum.getName());
        geraeteListView.setItems(FXCollections.observableArrayList(
                raum.getGeraete().stream()
                        .map(DAO::getName)
                        .collect(Collectors.toList())
        ));
    }

    public void setOnSchliessen(final Runnable onSchliessen) {
        this.onSchliessen = onSchliessen;
    }

    @FXML
    private void handleBearbeiten() {
    }

    @FXML
    private void handleSchliessen() {
        if (onSchliessen != null) onSchliessen.run();
    }
}