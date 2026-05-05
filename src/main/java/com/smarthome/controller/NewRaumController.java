package com.smarthome.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class NewRaumController {

    @FXML private TextField raumNameField;

    private Consumer<String> onNeuenRaumAnlegen;
    private Runnable onAbbrechen;

    public void setOnNeuenRaumAnlegen(final Consumer<String> onNeuenRaumAnlegen) {
        this.onNeuenRaumAnlegen = onNeuenRaumAnlegen;
    }

    public void setOnAbbrechen(final Runnable onAbbrechen) {
        this.onAbbrechen = onAbbrechen;
    }

    @FXML
    private void handleRaumAnlegen() {
        if (onNeuenRaumAnlegen != null) onNeuenRaumAnlegen.accept(raumNameField.getText());
    }

    @FXML
    private void handleAbbrechen() {
        if (onAbbrechen != null) onAbbrechen.run();
    }
}
