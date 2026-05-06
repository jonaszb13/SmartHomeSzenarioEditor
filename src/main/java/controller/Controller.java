package controller;

import data.models.Model;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import userInterface.View;
import util.customExceptions.MessageMissing;
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

        if (newValue != null) {
            final String fxmlFile = getFxmlFile(newValue);

            try {
                final Pane neuesPanel = FXMLLoader.load(
                        Objects.requireNonNull(getClass().getResource("/userInterface/" + fxmlFile))
                );
                view.getHauptPane().getChildren().setAll(neuesPanel);
            } catch (IOException e) {
                StatusLog.addError("FXMLLoader konnte nicht geladen werden", e);
            }

            //TODO dieser Aufruf muss in jeden changed (oder# einen generischeren)
            //TODO nur durchführen, wenn der Statusbereich sichtbar ist
            updateStatusLog();
        }
        //TODO Linebreak alle 50 Zeichen
        StatusLog.addError("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj jjjjjjjjjjjjjjjjjjjjjjjjjjjjjj jjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
    }

    private String getFxmlFile(TreeItem<String> newValue) {
        final TreeItem<String> root = view.getUebersichtTree().getRoot();
        final String fxmlFile;
        final TreeItem<String> parent = newValue.getParent();
        if (parent == root){
            fxmlFile = switch (newValue.getValue().strip()) {
                case "Räume" -> "raume-view.fxml";
                case "Geräte" -> "geraete-view.fxml";
                case "Szenarien" -> "szenarien-view.fxml";
                default -> throw new IllegalStateException("Unexpected value: " + newValue.getValue().strip());
            };
        //Raum
        } else if (parent == root.getChildren().get(0)) {
            fxmlFile = "raum-view.fxml";
        //Gerät
        } else if (parent == root.getChildren().get(1)) {
            fxmlFile = "geraet-view.fxml";
        //Szenario
        } else if (parent == root.getChildren().get(2)) {
            fxmlFile = "szenario-view.fxml";
        } else {
            fxmlFile = "haupt-view.fxml";
        }
        return fxmlFile;
    }

    private void updateStatusLog() {
        try {
            final List<Meldung> newMessages = model.getStatusbereich()
                    .getNewMessages(view.getStatusLogVBox()
                            .getChildren().isEmpty()
                            ? null : UUID.fromString(view.getStatusLogVBox()
                            .getChildren().getFirst().getUserData().toString()));
            newMessages.stream()
                    .map(meldung -> {
                        final Label label = new Label(meldung.getMeldungsTyp() + ": " + meldung.getMeldungstext());
                        label.setUserData(meldung.getMeldungsId());
                        label.setStyle(meldung.getMeldungsTyp()
                                .equals(Meldungstyp.FEHLER.getBezeichnung()) ? "-fx-text-fill: #cc0000"
                                : meldung.getMeldungsTyp()
                                .equals(Meldungstyp.METADATEN.getBezeichnung()) ? "-fx-text-fill: #0000ff" : "-fx-text-fill: #000000");
                        return label;
                    })
                    .forEach(view.getStatusLogVBox().getChildren()::addFirst);
        } catch (MessageMissing e) {
            StatusLog.addError(e.getMessage(), e);
        }
    }
}
