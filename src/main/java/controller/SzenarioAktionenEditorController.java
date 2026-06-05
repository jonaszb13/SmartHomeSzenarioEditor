package controller;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Szenario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import user_interface.WertControlFactory;

import java.util.List;
import java.util.UUID;

public class SzenarioAktionenEditorController {

    @FunctionalInterface
    public interface SpeichernHandler {
        void handle(Szenario.Aenderung aenderung);
    }

    @FXML
    private Label szenarioNameLabel;
    @FXML
    private TextField aktionsnameField;
    @FXML
    private ComboBox<Geraet> geraetComboBox;
    @FXML
    private ComboBox<String> schluesselComboBox;
    @FXML
    private VBox wertContainer;
    @FXML
    private Button speichernButton;

    private Szenario.Aenderung zuBearbeitendeAenderung;
    private Node aktuellesWertControl;

    private SpeichernHandler onSpeichern;
    private Runnable onAbbrechen;

    @FXML
    public void initialize() {
        geraetComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(final Geraet g) { return g == null ? "" : g.getName(); }
            @Override
            public Geraet fromString(final String s) { return null; }
        });
        geraetComboBox.valueProperty().addListener((obs, old, geraet) -> aktualisiereSchluesselAuswahl(geraet));
        schluesselComboBox.valueProperty().addListener((obs, old, _) -> aktualisiereWertControl());
    }

    public void setSzenarioName(final String name) {
        szenarioNameLabel.setText(name);
    }

    public void setGeraete(final List<Geraet> geraete) {
        geraetComboBox.setItems(FXCollections.observableArrayList(geraete));
    }

    public void setAenderung(final Szenario.Aenderung aenderung) {
        this.zuBearbeitendeAenderung = aenderung;
        if (aenderung == null) return;
        aktionsnameField.setText(aenderung.beschreibung());
        geraetComboBox.setValue(aenderung.geraet());
    }

    public void setButtonText(final String text) {
        speichernButton.setText(text);
    }

    public void setOnSpeichern(final SpeichernHandler handler) {
        this.onSpeichern = handler;
    }

    public void setOnAbbrechen(final Runnable handler) {
        this.onAbbrechen = handler;
    }

    private void aktualisiereSchluesselAuswahl(final Geraet geraet) {
        wertContainer.getChildren().clear();
        aktuellesWertControl = null;
        if (geraet == null) {
            schluesselComboBox.setItems(FXCollections.emptyObservableList());
            return;
        }
        schluesselComboBox.setItems(FXCollections.observableArrayList(geraet.getAttributTypen().keySet()));
        if (zuBearbeitendeAenderung != null && geraet.equals(zuBearbeitendeAenderung.geraet())) {
            schluesselComboBox.setValue(zuBearbeitendeAenderung.schluessel());
        } else if (!geraet.getAttributTypen().isEmpty()) {
            schluesselComboBox.getSelectionModel().selectFirst();
        }
    }

    private void aktualisiereWertControl() {
        wertContainer.getChildren().clear();
        aktuellesWertControl = null;
        final String schluessel = schluesselComboBox.getValue();
        final Geraet geraet = geraetComboBox.getValue();
        if (schluessel == null || geraet == null) return;

        final Class<?> typ = geraet.getAttributTypen().get(schluessel);
        aktuellesWertControl = WertControlFactory.erstelle(typ, resolveVorwert(geraet, schluessel));
        wertContainer.getChildren().add(aktuellesWertControl);
    }

    private String resolveVorwert(final Geraet geraet, final String schluessel) {
        if (zuBearbeitendeAenderung != null
                && geraet.equals(zuBearbeitendeAenderung.geraet())
                && schluessel.equals(zuBearbeitendeAenderung.schluessel())) {
            return zuBearbeitendeAenderung.wert();
        }
        return geraet.getValues().getOrDefault(schluessel, "");
    }

    private String leseWert() {
        return WertControlFactory.leseWert(aktuellesWertControl);
    }

    @FXML
    private void handleSpeichern() {
        final String aktionsname = aktionsnameField.getText();
        final Geraet geraet = geraetComboBox.getValue();
        final String schluessel = schluesselComboBox.getValue();
        if (aktionsname == null || aktionsname.isBlank() || geraet == null || schluessel == null || onSpeichern == null) return;
        final UUID id = zuBearbeitendeAenderung != null ? zuBearbeitendeAenderung.id() : UUID.randomUUID();
        onSpeichern.handle(new Szenario.Aenderung(id, geraet, aktionsname.trim(), schluessel, leseWert()));
    }

    @FXML
    private void handleAbbrechen() {
        if (onAbbrechen != null) onAbbrechen.run();
    }
}
