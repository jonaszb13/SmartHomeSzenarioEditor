package controller;

import data.models.fachobjekte.Raum;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import userinterface.DynamischeFelderBuilder;
import userinterface.RaumStringConverter;
import util.statusmeldungen.StatusLog;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class NeuesGeraetController {

    @FunctionalInterface
    public interface AnlegenHandler {
        void handle(String name, String art, Raum raum, Map<String, String> attributeMap);
    }

    @FXML private TextField nameField;
    @FXML private ComboBox<String> typComboBox;
    @FXML private ComboBox<Raum> raumComboBox;
    @FXML private VBox attributeContainer;

    private DynamischeFelderBuilder felderBuilder;
    private Function<String, Map<String, Class<?>>> attributTypenProvider;
    private AnlegenHandler onAnlegen;
    private Runnable onAbbrechen;
    private Runnable onValidierungsfehler;

    public void setGeraetTypen(final List<String> typen) {
        typComboBox.setItems(FXCollections.observableArrayList(typen));
    }

    public void setRaeume(final List<Raum> raeume) {
        raumComboBox.setItems(FXCollections.observableArrayList(raeume));
        raumComboBox.setConverter(new RaumStringConverter());
    }

    public void setAttributTypenProvider(final Function<String, Map<String, Class<?>>> provider) {
        this.attributTypenProvider = provider;
    }

    public void setOnAnlegen(final AnlegenHandler onAnlegen) {
        this.onAnlegen = onAnlegen;
    }

    public void setOnAbbrechen(final Runnable onAbbrechen) {
        this.onAbbrechen = onAbbrechen;
    }

    public void setOnValidierungsfehler(final Runnable onValidierungsfehler) {
        this.onValidierungsfehler = onValidierungsfehler;
    }

    @FXML
    private void onTypGeaendert() {
        final String typ = typComboBox.getValue();
        if (typ == null || attributTypenProvider == null) return;
        final Map<String, Class<?>> attributTypen = attributTypenProvider.apply(typ);
        felderBuilder = DynamischeFelderBuilder.fuerNeuesGeraet(attributTypen);
        attributeContainer.getChildren().setAll(felderBuilder.getVBox());
    }

    @FXML
    private void handleAnlegen() {
        final String name = nameField.getText();
        final String typ = typComboBox.getValue();
        final Raum raum = raumComboBox.getValue();
        if (name == null || name.isBlank() || typ == null || raum == null || felderBuilder == null) {
            StatusLog.addError("Alle Pflichtfelder (Name, Typ, Raum) müssen ausgefüllt sein.");
            if (onValidierungsfehler != null) onValidierungsfehler.run();
            return;
        }
        if (onAnlegen != null) onAnlegen.handle(name.trim(), typ, raum, felderBuilder.getWerte());
    }

    @FXML
    private void handleAbbrechen() {
        if (onAbbrechen != null) onAbbrechen.run();
    }
}
