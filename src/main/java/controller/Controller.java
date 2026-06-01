package controller;

import data.models.Model;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import userInterface.View;
import util.customExceptions.MessageMissing;
import util.statusmeldungen.StatusLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.function.Consumer;

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
        if (newValue == null) return;

        switch (view.getTreeItemType(newValue)) {
            case RAEUME   -> zeigeRaeumePanel();
            case RAUM     -> zeigeRaumDetail(model.getRaum(view.getRaumUuidForItem(newValue)));
            case SZENARIO -> zeigeSzenarioDetailPanel(model.getSzenario(view.getSzenarioUuidForItem(newValue)));
            default       -> StatusLog.addError(new InputMismatchException("Ausgewähltes Objekt existiert nicht."));
        }

        updateStatusLog();
    }

    private void aktualisiereTree() {
        view.updateTreeModel(model.getRaumMap(), model.getGeraete(), model.getSzenarioMap());
    }

    private void nachModelAenderung() {
        aktualisiereTree();
        updateStatusLog();
    }

    private void zeigePanelHelper(final String fxmlPfad, final Consumer<FXMLLoader> setup) {
        try {
            final FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/userInterface/" + fxmlPfad)));
            final Pane panel = loader.load();
            setup.accept(loader);
            view.setHauptPane(panel);
        } catch (IOException eIO) {
            StatusLog.addError("Panel konnte nicht geladen werden: " + fxmlPfad, eIO);
        }
    }

    private void zeigeRaeumePanel() {
        zeigePanelHelper("raeume-view.fxml", loader -> {
            final RaeumeController raeumeController = loader.getController();
            raeumeController.setRaeume(new ArrayList<>(model.getRaumMap().values()));
            raeumeController.setOnRaumOeffnen(this::zeigeRaumDetail);
            raeumeController.setOnNeuenRaumAnlegenRequested(this::zeigeNeuerRaumPanel);
            raeumeController.setOnAuswahlLoeschen(ids -> {
                ids.forEach(model::deleteRaum);
                nachModelAenderung();
                zeigeRaeumePanel();
            });
        });
    }

    private void zeigeRaumDetail(final Raum raum) {
        zeigePanelHelper("raum-view.fxml", loader -> {
            final RaumController raumController = loader.getController();
            raumController.setRaum(raum);
            raumController.setOnBearbeiten(() -> zeigeBearbeitenRaumPanel(raum));
            raumController.setOnSchliessen(this::zeigeRaeumePanel);
        });
    }

    private void zeigeNeuerRaumPanel() {
        zeigePanelHelper("neuer-raum-view.fxml", loader -> {
            final NeuerRaumController neuerRaumController = loader.getController();
            neuerRaumController.setOnAnlegen(name -> {
                model.addRaum(name);
                nachModelAenderung();
                zeigeRaeumePanel();
            });
            neuerRaumController.setOnAbbrechen(this::zeigeRaeumePanel);
        });
    }

    private void zeigeBearbeitenRaumPanel(final Raum raum) {
        zeigePanelHelper("edit-raum-view.fxml", loader -> {
            final BearbeitenRaumController bearbeitenRaumController = loader.getController();
            bearbeitenRaumController.setRaum(raum);
            bearbeitenRaumController.setOnSpeichern(neuerName -> {
                model.updateRaum(raum, neuerName);
                nachModelAenderung();
                zeigeRaumDetail(raum);
            });
            bearbeitenRaumController.setOnAbbrechen(() -> zeigeRaumDetail(raum));
            bearbeitenRaumController.setOnLoeschen(() -> {
                model.deleteRaum(raum.getId());
                nachModelAenderung();
                zeigeRaeumePanel();
            });
        });
    }

    private void zeigeSzenarioDetailPanel(final Szenario szenario) {
        zeigePanelHelper("szenario-view.fxml", loader -> {
            final SzenarioController szenarioController = loader.getController();
            szenarioController.setSzenario(szenario);
        });
    }

    private void updateStatusLog() {
        try {
            view.getStatusLogView().addMeldungen(
                    model.getStatusbereich().getNewMessages(view.getStatusLogView().getLetzteStatusMeldungsId()));
        } catch (MessageMissing e) {
            StatusLog.addError(e.getMessage(), e);
        }
    }
}
