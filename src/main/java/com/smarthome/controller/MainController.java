package com.smarthome.controller;

import com.smarthome.model.service.RaumService;
import com.smarthome.util.Meldung;
import com.smarthome.util.Meldungstyp;
import com.smarthome.util.StatusLog;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class MainController implements ChangeListener<TreeItem<String>> {

    @FXML private TreeView<String> uebersichtTree;
    @FXML private Pane hauptPane;
    @FXML private VBox statusLogVBox;

    private RaumService raumService;

    @FXML
    public void initialize() {
        uebersichtTree.setShowRoot(false);
        uebersichtTree.setRoot(erstelleNavigationsbaum());
        uebersichtTree.getSelectionModel().selectedItemProperty().addListener(this);
        StatusLog.addListener(this::zeigeStatusMeldung);
        ladeStandardPanel();
    }

    public void setRaumService(final RaumService raumService) {
        this.raumService = raumService;
    }

    private void zeigeStatusMeldung(final Meldung meldung) {
        Label label = new Label(meldung.getMeldungstext());
        if (meldung.getMeldungsTyp().equals(Meldungstyp.FEHLER.getBezeichnung())) {
            label.setStyle("-fx-text-fill: #cc0000;");
        }
        Platform.runLater(() -> statusLogVBox.getChildren().add(label));
    }

    private TreeItem<String> erstelleNavigationsbaum() {
        TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);
        root.getChildren().addAll(
                new TreeItem<>("Räume"),
                new TreeItem<>("Geräte"),
                new TreeItem<>("Szenarien")
        );
        return root;
    }

    private void ladeStandardPanel() {
        try {
            Pane standardPanel = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource("/org/example/ui/haupt-view.fxml")));
            hauptPane.getChildren().setAll(standardPanel);
        } catch (IOException e) {
            StatusLog.addError("Standardansicht konnte nicht geladen werden", e);
        }
    }

    @Override
    public void changed(final ObservableValue<? extends TreeItem<String>> observable,
                        final TreeItem<String> oldValue, final TreeItem<String> newValue) {
        if (newValue == null) return;

        String fxmlFile = switch (newValue.getValue().strip()) {
            case "Räume"     -> "raum-view.fxml";
            case "Geräte"    -> "geraete-view.fxml";
            case "Szenarien" -> "szenarien-view.fxml";
            default          -> "haupt-view.fxml";
        };

        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/org/example/ui/" + fxmlFile)));
            Pane neuesPanel = loader.load();

            if (loader.getController() instanceof RaumController raumController) {
                raumController.setRaumService(raumService);
                raumController.setPanelWechseln(pane -> hauptPane.getChildren().setAll(pane));
            }

            hauptPane.getChildren().setAll(neuesPanel);
        } catch (IOException e) {
            StatusLog.addError("FXMLLoader konnte nicht geladen werden", e);
        }
    }
}
