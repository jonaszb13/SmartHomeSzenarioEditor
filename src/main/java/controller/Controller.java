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
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Controller implements ChangeListener<TreeItem<String>> {
    private final View view;
    private final Model model;

    public Controller(final View view, final Model model) {
        this.view = view;
        this.view.addUebersichtTreeSelectionListener(this);
        this.model = model;
    }

    @Override
    public void changed(final ObservableValue<? extends TreeItem<String>> observable,
                        final TreeItem<String> oldValue, final TreeItem<String> newValue) {

        if (newValue != null) {
            final String fxmlFile = getFxmlFile(newValue);
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/userInterface/" + fxmlFile)));
                final Pane neuesPanel = loader.load();
                switch (view.getTreeItemType(newValue)) {
                    case RAUM -> {
                        final RaumController raumController = loader.getController();
                        raumController.setRaum(model.getRaum(view.getRaumUuidForItem(newValue)));
                    }
                    case SZENARIO -> {
                        final SzenarioController szenarioController = loader.getController();
                        szenarioController.setSzenario(model.getSzenario(view.getSzenarioUuidForItem(newValue)));
                    }
                    default -> StatusLog.addError(new InputMismatchException("Ausgewähltes Objekt existiert nicht."));
                }
                view.getHauptPane().getChildren().setAll(neuesPanel);
            } catch (IOException eIO) {
                StatusLog.addError("FXMLLoader konnte nicht geladen werden", eIO);
            }

            //TODO dieser Aufruf muss in jeden changed (oder# einen generischeren)
            //TODO nur durchführen, wenn der Statusbereich sichtbar ist
            updateStatusLog();
        }
        //TODO Linebreak alle 50 Zeichen
        //StatusLog.addError("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj jjjjjjjjjjjjjjjjjjjjjjjjjjjjjj jjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
        //TODO Debugging entfernen
        StatusLog.createErrorFile();
    }

    private String getFxmlFile(final TreeItem<String> newValue) {
        return switch (view.getTreeItemType(newValue)) {
            case RAUM -> "raum-view.fxml";
            case GERAET -> "geraet-view.fxml";
            case SZENARIO -> "szenario-view.fxml";
            case RAEUME -> "raeume-view.fxml";
            case GERAETE -> "geraete-view.fxml";
            case SZENARIEN -> "szenarien-view.fxml";
            default -> "haupt-view.fxml";
        };
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
                        final String typ = meldung.getMeldungsTyp();
                        if (typ.equals(Meldungstyp.FEHLER.getBezeichnung())) {
                            label.setStyle("-fx-text-fill: #cc0000");
                        } else if (typ.equals(Meldungstyp.METADATEN.getBezeichnung())) {
                            label.setStyle("-fx-text-fill: #0000ff");
                        } else label.setStyle("-fx-text-fill: #000000");
                        return label;
                    })
                    .forEach(view.getStatusLogVBox().getChildren()::addFirst);
        } catch (MessageMissing e) {
            StatusLog.addError(e.getMessage(), e);
        }
    }
}
