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

    public Controller(final View view, final Model model) {
        this.view = view;
        this.model = model;
        this.view.addUebersichtTreeSelectionListener(this);
    }

    @Override
    public void changed(final ObservableValue<? extends TreeItem<String>> observable,
                        final TreeItem<String> oldValue, final TreeItem<String> newValue) {
        if (newValue == null) return;

        final String fxmlFile = getFxmlFile(newValue);
        try {
            final FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/userInterface/" + fxmlFile)));
            final Pane neuesPanel = loader.load();

            switch (view.getTreeItemType(newValue)) {
                case RAUM -> {
                    final RaumController raumController = loader.getController();
                    raumController.setRaum(model.getRaum(view.getUuidForItem(newValue)));
                }
                case SZENARIO -> {
                    final SzenarioController szenarioController = loader.getController();
                    szenarioController.setSzenario(model.getSzenario(view.getUuidForItem(newValue)));
                }
                default -> { }
            }

            view.getHauptPane().getChildren().setAll(neuesPanel);
        } catch (IOException e) {
            StatusLog.addError("FXMLLoader konnte nicht geladen werden", e);
        }

        updateStatusLog();
    }

    private String getFxmlFile(final TreeItem<String> item) {
        return switch (view.getTreeItemType(item)) {
            case RAEUME_CATEGORY -> "raume-view.fxml";
            case GERAETE_CATEGORY -> "geraete-view.fxml";
            case SZENARIEN_CATEGORY -> "szenarien-view.fxml";
            case RAUM -> "raum-view.fxml";
            case GERAET -> "geraet-view.fxml";
            case SZENARIO -> "szenario-view.fxml";
            case UNKNOWN -> "haupt-view.fxml";
        };
    }

    private void updateStatusLog() {
        try {
            final UUID letzteId = view.getStatusLogVBox().getChildren().isEmpty()
                    ? null
                    : UUID.fromString(view.getStatusLogVBox().getChildren().getFirst().getUserData().toString());
            final List<Meldung> newMessages = model.getStatusbereich().getNewMessages(letzteId);
            newMessages.stream()
                    .map(meldung -> {
                        final Label label = new Label(meldung.getMeldungsTyp() + ": " + meldung.getMeldungstext());
                        label.setUserData(meldung.getMeldungsId());
                        final String typ = meldung.getMeldungsTyp();
                        if (typ.equals(Meldungstyp.FEHLER.getBezeichnung())) {
                            label.setStyle("-fx-text-fill: #cc0000");
                        } else if (typ.equals(Meldungstyp.METADATEN.getBezeichnung())) {
                            label.setStyle("-fx-text-fill: #0000ff");
                        }
                        return label;
                    })
                    .forEach(view.getStatusLogVBox().getChildren()::addFirst);
        } catch (MessageMissing e) {
            StatusLog.addError(e.getMessage(), e);
        }
    }
}
