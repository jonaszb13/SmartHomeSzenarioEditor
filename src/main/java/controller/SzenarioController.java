package controller;

import data.models.fachobjekte.Szenario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Map;
import java.util.stream.Collectors;

public class SzenarioController {

    @FXML
    private TextField szenarioNameField;
    @FXML
    private TextArea szenarioBeschreibungArea;
    @FXML
    private ListView<String> szenarioInhalt;
    @FXML
    private Label szenarioNameLabel;

    private Runnable onBearbeiten;
    private Runnable onSchliessen;
    private Runnable onAusfuehren;

    public void setSzenario(final Szenario szenario) {
        szenarioNameLabel.setText(szenario.getName());
        szenarioNameField.setText(szenario.getName());
        szenarioBeschreibungArea.setText(szenario.getBeschreibung() != null ? szenario.getBeschreibung() : "");
        szenarioInhalt.setItems(FXCollections.observableArrayList(
                szenario.getAenderungen().entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(e -> e.getKey() + ". " + e.getValue().beschreibung()
                                + "  [" + e.getValue().geraet().getName()
                                + ": " + e.getValue().schluessel() + " = " + e.getValue().wert() + "]")
                        .collect(Collectors.toList())
        ));
    }

    public void setOnBearbeiten(final Runnable onBearbeiten) {
        this.onBearbeiten = onBearbeiten;
    }

    public void setOnSchliessen(final Runnable onSchliessen) {
        this.onSchliessen = onSchliessen;
    }

    public void setOnAusfuehren(final Runnable onAusfuehren) {
        this.onAusfuehren = onAusfuehren;
    }

    @FXML
    private void handleBearbeiten() {
        if (onBearbeiten != null) onBearbeiten.run();
    }

    @FXML
    private void handleSchliessen() {
        if (onSchliessen != null) onSchliessen.run();
    }

    @FXML
    private void handleAusfuehren() {
        if (onAusfuehren != null) onAusfuehren.run();
    }
}
