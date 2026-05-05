package com.smarthome.controller;

import com.smarthome.model.entity.Raum;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class EditRaumController {

    @FXML private Label raumNameLabel;
    @FXML private TextField editRaumNameField;

    private Consumer<String> onRaumBearbeiten;
    private Runnable onAbbrechen;

    public void setOnRaumBearbeiten(final Consumer<String> onRaumBearbeiten) {
        this.onRaumBearbeiten = onRaumBearbeiten;
    }

    public void setOnAbbrechen(final Runnable onAbbrechen) {
        this.onAbbrechen = onAbbrechen;
    }

    public void setRaum(final Raum raum) {
        raumNameLabel.setText(raum.getName());
        editRaumNameField.setText(raum.getName());
    }

    @FXML
    private void handleRaumBearbeiten() {
        if (onRaumBearbeiten != null) onRaumBearbeiten.accept(editRaumNameField.getText());
    }

    @FXML
    private void handleAbbrechen() {
        if (onAbbrechen != null) onAbbrechen.run();
    }
}
