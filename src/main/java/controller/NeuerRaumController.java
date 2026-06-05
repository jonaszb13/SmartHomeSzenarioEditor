package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import util.statusmeldungen.StatusLog;

import java.util.function.Consumer;

public class NeuerRaumController {

    @FXML private TextField raumNameField;

    private Consumer<String> onAnlegen;
    private Runnable onAbbrechen;
    private Runnable onValidierungsfehler;

    public void setOnAnlegen(final Consumer<String> onAnlegen) {
        this.onAnlegen = onAnlegen;
    }

    public void setOnAbbrechen(final Runnable onAbbrechen) {
        this.onAbbrechen = onAbbrechen;
    }

    public void setOnValidierungsfehler(final Runnable onValidierungsfehler) {
        this.onValidierungsfehler = onValidierungsfehler;
    }

    @FXML
    private void handleAnlegen() {
        final String name = raumNameField.getText();
        if (name == null || name.isBlank()) {
            StatusLog.addError("Pflichtfeld 'Name' muss ausgefüllt sein.");
            if (onValidierungsfehler != null) onValidierungsfehler.run();
            return;
        }
        if (onAnlegen != null) onAnlegen.accept(name.trim());
    }

    @FXML
    private void handleAbbrechen() {
        if (onAbbrechen != null) onAbbrechen.run();
    }
}
