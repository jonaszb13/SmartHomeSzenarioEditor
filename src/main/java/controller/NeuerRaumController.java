package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class NeuerRaumController {

    @FXML private TextField raumNameField;

    private Consumer<String> onAnlegen;
    private Runnable onAbbrechen;

    public void setOnAnlegen(final Consumer<String> onAnlegen) {
        this.onAnlegen = onAnlegen;
    }

    public void setOnAbbrechen(final Runnable onAbbrechen) {
        this.onAbbrechen = onAbbrechen;
    }

    @FXML
    private void handleAnlegen() {
        final String name = raumNameField.getText();
        if (name != null && !name.isBlank() && onAnlegen != null) {
            onAnlegen.accept(name.trim());
        }
    }

    @FXML
    private void handleAbbrechen() {
        if (onAbbrechen != null) onAbbrechen.run();
    }
}
