package controller;

import data.models.fachobjekte.Szenario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import userinterface.AktionListCell;

import java.util.ArrayList;
import java.util.List;

public class BearbeitenSzenarioController {

    @FunctionalInterface
    public interface AktionEditorHandler {
        void handle(String name, String beschreibung, List<Szenario.Aenderung> aktionen, Integer index);
    }

    @FunctionalInterface
    public interface SpeichernHandler {
        void handle(String name, String beschreibung, List<Szenario.Aenderung> aktionen);
    }

    @FXML
    private Label szenarioNameKopf;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea beschreibungField;
    @FXML
    private ListView<Szenario.Aenderung> aktionenListView;

    private final ObservableList<Szenario.Aenderung> aktionen = FXCollections.observableArrayList();

    private AktionEditorHandler onAktionEditor;
    private SpeichernHandler onSpeichern;
    private Runnable onAbbrechen;
    private Runnable onLoeschen;

    @FXML
    public void initialize() {
        aktionenListView.setItems(aktionen);
        aktionenListView.setCellFactory(_ -> new AktionListCell(aktionen, this::swapAktionen));
    }

    public void setInitialState(final String name, final String beschreibung, final List<Szenario.Aenderung> aktionen) {
        szenarioNameKopf.setText(name);
        nameField.setText(name);
        beschreibungField.setText(beschreibung);
        this.aktionen.setAll(aktionen);
    }

    public void setOnAktionEditor(final AktionEditorHandler handler) {
        this.onAktionEditor = handler;
    }

    public void setOnSpeichern(final SpeichernHandler handler) {
        this.onSpeichern = handler;
    }

    public void setOnAbbrechen(final Runnable handler) {
        this.onAbbrechen = handler;
    }

    public void setOnLoeschen(final Runnable handler) {
        this.onLoeschen = handler;
    }

    @FXML
    private void handleAktionHinzufuegen() {
        if (onAktionEditor != null) {
            onAktionEditor.handle(nameField.getText(), beschreibungField.getText(), new ArrayList<>(aktionen), null);
        }
    }

    @FXML
    private void handleAktionBearbeiten() {
        final int idx = aktionenListView.getSelectionModel().getSelectedIndex();
        if (idx < 0 || onAktionEditor == null) return;
        onAktionEditor.handle(nameField.getText(), beschreibungField.getText(), new ArrayList<>(aktionen), idx);
    }

    @FXML
    private void handleAktionLoeschen() {
        final int idx = aktionenListView.getSelectionModel().getSelectedIndex();
        if (idx >= 0) aktionen.remove(idx);
    }

    @FXML
    private void handleSpeichern() {
        final String name = nameField.getText();
        if (name == null || name.isBlank() || onSpeichern == null) return;
        onSpeichern.handle(name.trim(), beschreibungField.getText().trim(), new ArrayList<>(aktionen));
    }

    @FXML
    private void handleAbbrechen() {
        if (onAbbrechen != null) onAbbrechen.run();
    }

    @FXML
    private void handleLoeschen() {
        if (onLoeschen != null) onLoeschen.run();
    }

    private void swapAktionen(final int i, final int j) {
        final Szenario.Aenderung tmp = aktionen.get(i);
        aktionen.set(i, aktionen.get(j));
        aktionen.set(j, tmp);
    }
}
