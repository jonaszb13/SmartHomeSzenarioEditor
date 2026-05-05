package com.smarthome.controller;

import com.smarthome.model.entity.Raum;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.stream.Collectors;

public class InfoRaumController {

    @FXML private Label raumNameLabel;
    @FXML private TextField raumNameField;
    @FXML private ListView<String> geraeteListView;

    private Runnable onSchliessen;

    public void setRaum(final Raum raum) {
        raumNameLabel.setText(raum.getName());
        raumNameField.setText(raum.getName());
        geraeteListView.setItems(FXCollections.observableArrayList(
                raum.getGeraete().stream()
                        .map(g -> g.getName())
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
