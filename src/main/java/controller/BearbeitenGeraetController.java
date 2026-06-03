package controller;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import userInterface.DynamischeFelderBuilder;
import userInterface.RaumStringConverter;

import java.util.List;
import java.util.Map;

public class BearbeitenGeraetController {

    @FunctionalInterface
    public interface SpeichernHandler {
        void handle(String name, Raum raum, Map<String, String> attributeMap);
    }

    @FXML private Label geraetNameKopf;
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField typField;
    @FXML private ComboBox<Raum> raumComboBox;
    @FXML private VBox attributeContainer;

    private DynamischeFelderBuilder felderBuilder;
    private SpeichernHandler onSpeichern;
    private Runnable onAbbrechen;
    private Runnable onLoeschen;

    public void setGeraet(final Geraet geraet) {
        geraetNameKopf.setText(geraet.getName());
        idField.setText(geraet.getId().toString());
        nameField.setText(geraet.getName());
        typField.setText(geraet.getClass().getSimpleName());

        felderBuilder = DynamischeFelderBuilder.fuerBearbeitung(geraet.getAttributTypen(), geraet.getValues());
        attributeContainer.getChildren().setAll(felderBuilder.getVBox());
    }

    public void setRaeume(final List<Raum> raeume, final Raum aktuellerRaum) {
        raumComboBox.setItems(FXCollections.observableArrayList(raeume));
        raumComboBox.setConverter(new RaumStringConverter());
        raumComboBox.setValue(aktuellerRaum);
    }

    public void setOnSpeichern(final SpeichernHandler onSpeichern) {
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
        final String name = nameField.getText();
        final Raum raum = raumComboBox.getValue();
        if (name == null || name.isBlank() || raum == null || felderBuilder == null) return;
        if (onSpeichern != null) onSpeichern.handle(name.trim(), raum, felderBuilder.getWerte());
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
