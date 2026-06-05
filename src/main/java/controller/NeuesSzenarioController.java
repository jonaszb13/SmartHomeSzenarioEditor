package controller;

import data.models.fachobjekte.Szenario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import userinterface.AktionListCell;

import java.util.ArrayList;
import java.util.List;

public class NeuesSzenarioController {

    @FunctionalInterface
    public interface AktionHinzufuegenHandler {
        void handle(String name, String beschreibung, List<Szenario.Aenderung> aktionen);
    }

    @FunctionalInterface
    public interface AktionBearbeitenHandler {
        void handle(String name, String beschreibung, List<Szenario.Aenderung> aktionen, int index);
    }

    @FunctionalInterface
    public interface AnlegenHandler {
        void handle(String name, String beschreibung, List<Szenario.Aenderung> aktionen);
    }

    @FXML
    private TextField nameField;
    @FXML
    private TextArea beschreibungField;
    @FXML
    private ListView<Szenario.Aenderung> aktionenListView;

    private final ObservableList<Szenario.Aenderung> aktionen = FXCollections.observableArrayList();

    private AktionHinzufuegenHandler onAktionHinzufuegen;
    private AktionBearbeitenHandler onAktionBearbeiten;
    private AnlegenHandler onAnlegen;
    private Runnable onAbbrechen;

    @FXML
    public void initialize() {
        aktionenListView.setItems(aktionen);
        aktionenListView.setCellFactory(_ -> new AktionListCell(aktionen, this::swapAktionen));
    }

    public void setInitialState(final String name, final String beschreibung, final List<Szenario.Aenderung> aktionen) {
        nameField.setText(name);
        beschreibungField.setText(beschreibung);
        this.aktionen.setAll(aktionen);
    }

    public void setOnAktionHinzufuegen(final AktionHinzufuegenHandler handler) {
        this.onAktionHinzufuegen = handler;
    }

    public void setOnAktionBearbeiten(final AktionBearbeitenHandler handler) {
        this.onAktionBearbeiten = handler;
    }

    public void setOnAnlegen(final AnlegenHandler handler) {
        this.onAnlegen = handler;
    }

    public void setOnAbbrechen(final Runnable handler) {
        this.onAbbrechen = handler;
    }

    @FXML
    private void handleAktionHinzufuegen() {
        if (onAktionHinzufuegen != null) {
            onAktionHinzufuegen.handle(nameField.getText(), beschreibungField.getText(), new ArrayList<>(aktionen));
        }
    }

    @FXML
    private void handleAktionBearbeiten() {
        final int idx = aktionenListView.getSelectionModel().getSelectedIndex();
        if (idx < 0 || onAktionBearbeiten == null) return;
        onAktionBearbeiten.handle(nameField.getText(), beschreibungField.getText(), new ArrayList<>(aktionen), idx);
    }

    @FXML
    private void handleAktionLoeschen() {
        final int idx = aktionenListView.getSelectionModel().getSelectedIndex();
        if (idx >= 0) aktionen.remove(idx);
    }

    @FXML
    private void handleAnlegen() {
        final String name = nameField.getText();
        if (name == null || name.isBlank() || onAnlegen == null) return;
        onAnlegen.handle(name.trim(), beschreibungField.getText().trim(), new ArrayList<>(aktionen));
    }

    @FXML
    private void handleAbbrechen() {
        if (onAbbrechen != null) onAbbrechen.run();
    }

    private void swapAktionen(final int i, final int j) {
        final Szenario.Aenderung tmp = aktionen.get(i);
        aktionen.set(i, aktionen.get(j));
        aktionen.set(j, tmp);
    }
}
