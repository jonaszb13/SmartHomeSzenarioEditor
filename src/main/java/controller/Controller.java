package controller;

import data.models.Model;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
 import javafx.scene.paint.Color;
import userInterface.View;
import util.statusmeldungen.Meldung;
import util.statusmeldungen.Meldungstyp;
import util.statusmeldungen.StatusLog;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Controller implements ChangeListener<TreeItem<String>> {
    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.view.addUebersichtTreeSelectionListener(this);
        this.model = model;
    }

    @Override
    public void changed(ObservableValue<? extends TreeItem<String>> observable,
                        TreeItem<String> oldValue, TreeItem<String> newValue) {

        if (newValue == null) return;

        String fxmlFile = switch (newValue.getValue().strip()) {
            case "Räume"     -> "raum-view.fxml";
            case "Geräte"    -> "geraete-view.fxml";
            case "Szenarien" -> "szenarien-view.fxml";
            default          -> "haupt-view.fxml";
        };

        try {
            Pane neuesPanel = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource("/userInterface/" + fxmlFile))
            );
            view.getHauptPane().getChildren().setAll(neuesPanel);
        } catch (IOException e) {
            StatusLog.addError("FXMLLoader konnte nicht geladen werden", e);
        }

        //TODO nur für Testen
        StatusLog.addHinweis("Hinweis");
        StatusLog.addError("Error");
        StatusLog.addMetadaten("Meta");
        //TODO dieser Aufruf muss in jeden changed (oder einen generischeren)
        //TODO nur durchführen, wenn der Statusbereich sichtbar ist
        updateStatusLog();
    }

    private void updateStatusLog() {
        List<Meldung> newMessages = model.getStatusbereich()
                .getNewMessages(view.getStatusLogVBox()
                        .getChildren().isEmpty()
                        ? null : UUID.fromString(view.getStatusLogVBox()
                        .getChildren().getFirst().getUserData().toString()));
        newMessages.stream()
                .map(meldung -> {
                    Label label = new Label(meldung.getMeldungsTyp() + ": " + meldung.getMeldungstext());
                    label.setUserData(meldung.getMeldungsId());
                    label.setStyle(meldung.getMeldungsTyp()
                            .equals(Meldungstyp.FEHLER.getBezeichnung()) ? "-fx-text-fill: #cc0000"
                            : meldung.getMeldungsTyp()
                            .equals(Meldungstyp.METADATEN.getBezeichnung()) ? "-fx-text-fill: #0000ff" : "-fx-text-fill: #000000");
                    return label;
        })
                .forEach(view.getStatusLogVBox().getChildren()::addFirst);
    }
}
