package controller;

import data.models.fachobjekte.Szenario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.util.stream.Collectors;

public class SzenarioController {

    @FXML
    private TextField szenarioNameField;
    @FXML
    private ListView<String> szenarioInhalt;
    @FXML
    private Label szenrioNameLabel;

    private static SzenarioController instance;

    public static SzenarioController getInstance() {
        if (instance == null) {
            instance = new SzenarioController();
            instance.szenarioNameField = new TextField();
            instance.szenarioInhalt = new ListView<>();
            instance.szenrioNameLabel = new Label();
        }
        return instance;
    }

    public void setSzenario(final Szenario szenario) {
        szenarioNameField.setText(szenario.getName());
        szenrioNameLabel.setText(szenario.getName());
        szenarioInhalt.setItems(FXCollections.observableArrayList(szenario.getAenderungen().values().stream().map(Szenario.Aenderung::beschreibung).collect(Collectors.toList())));
    }

    @FXML
    private void handleBearbeiten() {
    }
}
