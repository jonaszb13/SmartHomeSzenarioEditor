package controller;

import data.models.fachobjekte.Raum;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class BearbeitenRaumController {

    //TODO: vielleicht alle fxml Controller in das userInterface package verschieben, da hier ja keine keine wirkliche logik drin ist

    @FXML private Label raumNameKopf;
    @FXML private TextField raumNameField;

    private Consumer<String> onSpeichern;
    private Runnable onAbbrechen;
    private Runnable onLoeschen;

    public void setRaum(final Raum raum) {
        raumNameKopf.setText(raum.getName());
        raumNameField.setText(raum.getName());
    }

    public void setOnSpeichern(final Consumer<String> onSpeichern) {
        this.onSpeichern = onSpeichern;
    }

    public void setOnAbbrechen(final Runnable onAbbrechen) {
        this.onAbbrechen = onAbbrechen;
    }

    public void setOnLoeschen(final Runnable onLoeschen) {
        this.onLoeschen = onLoeschen;
    }

    @FXML
    private void handleSpeichern() {
        final String neuerName = raumNameField.getText();
        if (neuerName != null && !neuerName.isBlank() && onSpeichern != null) {
            onSpeichern.accept(neuerName.trim());
        }
    }

    @FXML
    private void handleAbbrechen() {
        if (onAbbrechen != null) onAbbrechen.run();
    }

    @FXML
    private void handleLoeschen() {
        if (onLoeschen != null) onLoeschen.run();
    }
}
