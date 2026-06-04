package controller;

import data.models.Model;
import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import data.services.datenServices.DataTransportService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.sql.SQLException;
import userInterface.View;
import util.customExceptions.MessageMissingException;
import util.statusmeldungen.StatusLog;

import java.io.File;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class Controller implements ChangeListener<TreeItem<String>> {
    private final View view;
    private final Model model;

    private final SzenarioFormState formState = new SzenarioFormState();
    private final DataTransportService dataTransportService = new DataTransportService();

    public Controller(final View view, final Model model) {
        this.view = view;
        this.view.addUebersichtTreeSelectionListener(this);
        this.model = model;
        aktualisiereSzenarioMenu();
        registriereMenuAktionen();
    }

    public void zeigeStandardansicht() {
        zeigeSzenarienPanel();
    }

    private void registriereMenuAktionen() {
        view.getMenuNeu().setOnAction(e -> handleNew());
        view.getMenuExport().setOnAction(e -> handleExport());
        view.getMenuImport().setOnAction(e -> handleImport());
    }

    private void handleExport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Datenauszug exportieren");
        chooser.setInitialFileName("Datenauszug_" + System.currentTimeMillis() + ".csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-Dateien", "*.csv"));
        File file = chooser.showSaveDialog(view.getWindow());
        if (file == null) return;
        if (dataTransportService.exportData(file)) {
            StatusLog.addHinweis("Datenauszug exportiert: " + file.getName());
        }
        updateStatusLog();
    }

    private void handleImport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Datenauszug importieren");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-Dateien", "*.csv"));
        File file = chooser.showOpenDialog(view.getWindow());
        if (file == null) return;
        if (dataTransportService.importData(file)) {
            StatusLog.addHinweis("Datenauszug importiert: " + file.getName());
            model.reload();
            nachModelAenderung();
        } else {
            updateStatusLog();
        }
    }

    private void handleNew() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Alle Daten löschen");
        confirm.setHeaderText("Alle Daten löschen?");
        confirm.setContentText("Räume, Geräte und Szenarien werden gelöscht.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dataTransportService.clearAllData();
                    StatusLog.addHinweis("Alle Daten wurden gelöscht.");
                    model.reload();
                    nachModelAenderung();
                } catch (SQLException e) {
                    StatusLog.addError("Daten konnten nicht gelöscht werden", e);
                    updateStatusLog();
                }
            }
        });
    }

    @Override
    public void changed(final ObservableValue<? extends TreeItem<String>> observable,
                        final TreeItem<String> oldValue, final TreeItem<String> newValue) {
        if (newValue == null) return;

        switch (view.getTreeItemType(newValue)) {
            case RAEUME   -> zeigeRaeumePanel();
            case RAUM     -> zeigeRaumDetail(model.getRaum(view.getRaumUuidForItem(newValue)));
            case GERAETE  -> zeigeGeraetePanel();
            case GERAET   -> zeigeGeraetDetail(model.getGeraet(view.getGeraetUuidForItem(newValue)));
            case SZENARIO -> zeigeSzenarioDetailPanel(model.getSzenario(view.getSzenarioUuidForItem(newValue)));
            case SZENARIEN -> zeigeSzenarienPanel();
            default       -> StatusLog.addError(new InputMismatchException("Ausgewähltes Objekt existiert nicht."));
        }

        updateStatusLog();
    }

    private void aktualisiereTree() {
        view.updateTreeModel(model.getRaumMap(), model.getGeraete(), model.getSzenarioMap());
    }

    private void nachModelAenderung() {
        aktualisiereTree();
        aktualisiereSzenarioMenu();
        updateStatusLog();
    }

    private void aktualisiereSzenarioMenu() {
        view.getSzenarioOeffnenMenu().getItems().clear();
        model.getSzenarioMap().forEach((id, szenario) -> {
            final MenuItem item = new MenuItem(szenario.getName());
            item.setOnAction(e -> {
                model.aktiviereSzenario(szenario);
                nachModelAenderung();
            });
            view.getSzenarioOeffnenMenu().getItems().add(item);
        });
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

    //TODO: Controller vielleich aufteilen in eigenen Raum, Geräte und Szenario Controller und von diesem hier nur noch delegieren

    private void zeigeGeraetePanel() {
        zeigePanelHelper("geraete-view.fxml", loader -> {
            final GeraeteController geraeteController = loader.getController();
            geraeteController.setGeraete(new ArrayList<>(model.getGeraete().values()));
            geraeteController.setOnGeraetOeffnen(this::zeigeGeraetDetail);
            geraeteController.setOnNeuenGeraetAnlegenRequested(this::zeigeNeuesGeraetPanel);
            geraeteController.setOnAuswahlLoeschen(ids -> {
                ids.forEach(model::deleteGeraet);
                nachModelAenderung();
                zeigeGeraetePanel();
            });
        });
    }

    private void zeigeGeraetDetail(final Geraet geraet) {
        zeigePanelHelper("geraet-view.fxml", loader -> {
            final GeraetController geraetController = loader.getController();
            geraetController.setGeraet(geraet);
            geraetController.setOnBearbeiten(() -> zeigeBearbeitenGeraetPanel(geraet));
            geraetController.setOnAbbrechen(this::zeigeGeraetePanel);
        });
    }

    private void zeigeNeuesGeraetPanel() {
        zeigePanelHelper("neues-geraet-view.fxml", loader -> {
            final NeuesGeraetController neuesGeraetController = loader.getController();
            neuesGeraetController.setGeraetTypen(new ArrayList<>(model.getGeraeteTypen()));
            neuesGeraetController.setRaeume(new ArrayList<>(model.getRaumMap().values()));
            neuesGeraetController.setAttributTypenProvider(model::getAttributTypenFuerGeraetTyp);
            neuesGeraetController.setOnAnlegen((name, art, raum, attributeMap) -> {
                model.addGeraet(name, art, raum, attributeMap);
                nachModelAenderung();
                zeigeGeraetePanel();
            });
            neuesGeraetController.setOnAbbrechen(this::zeigeGeraetePanel);
        });
    }

    private void zeigeBearbeitenGeraetPanel(final Geraet geraet) {
        zeigePanelHelper("edit-geraet-view.fxml", loader -> {
            final BearbeitenGeraetController bearbeitenGeraetController = loader.getController();
            bearbeitenGeraetController.setGeraet(geraet);
            bearbeitenGeraetController.setRaeume(new ArrayList<>(model.getRaumMap().values()), geraet.getRaum());
            bearbeitenGeraetController.setOnSpeichern((name, raum, attributeMap) -> {
                model.updateGeraetName(geraet, name);
                model.updateGeraetRaum(geraet, raum);
                model.updateGeraetWerte(geraet, attributeMap);
                nachModelAenderung();
                zeigeGeraetDetail(geraet);
            });
            bearbeitenGeraetController.setOnAbbrechen(() -> zeigeGeraetDetail(geraet));
            bearbeitenGeraetController.setOnLoeschen(() -> {
                model.deleteGeraet(geraet.getId());
                nachModelAenderung();
                zeigeGeraetePanel();
            });
        });
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

    private void zeigeSzenarienPanel() {
        zeigePanelHelper("szenarien-view.fxml", loader -> {
            final SzenarienController szenarienController = loader.getController();
            szenarienController.setSzenarien(new ArrayList<>(model.getSzenarioMap().values()));
            szenarienController.setOnSzenarioOeffnen(this::zeigeSzenarioDetailPanel);
            szenarienController.setOnSzenarioAusfuehren(szenario -> {
                model.aktiviereSzenario(szenario);
                nachModelAenderung();
            });
            szenarienController.setOnNeuesSzenarioAnlegenRequested(this::zeigeNeuesSzenarioPanelFrisch);
            szenarienController.setOnAuswahlLoeschen(ids -> {
                ids.forEach(id -> model.deleteSzenario(model.getSzenario(id)));
                nachModelAenderung();
                zeigeSzenarienPanel();
            });
        });
    }

    private void zeigeSzenarioDetailPanel(final Szenario szenario) {
        zeigePanelHelper("szenario-view.fxml", loader -> {
            final SzenarioController szenarioController = loader.getController();
            szenarioController.setSzenario(szenario);
            szenarioController.setOnBearbeiten(() -> zeigeBearbeitenSzenarioPanel(szenario));
            szenarioController.setOnSchliessen(this::zeigeSzenarienPanel);
            szenarioController.setOnAusfuehren(() -> {
                model.aktiviereSzenario(szenario);
                nachModelAenderung();
            });
        });
    }

    private void zeigeNeuesSzenarioPanelFrisch() {
        formState.zuruecksetzen();
        zeigeNeuesSzenarioPanelMitState();
    }

    private void zeigeNeuesSzenarioPanelMitState() {
        zeigePanelHelper("neues-szenario-view.fxml", loader -> {
            final NeuesSzenarioController ctrl = loader.getController();
            ctrl.setInitialState(formState.name, formState.beschreibung, new ArrayList<>(formState.aktionen));
            ctrl.setOnAktionHinzufuegen((name, beschr, aktionen) -> {
                formState.setze(name, beschr, aktionen);
                formState.editAktionIndex = null;
                zeigeAktionenEditorPanel(null);
            });
            ctrl.setOnAktionBearbeiten((name, beschr, aktionen, idx) -> {
                formState.setze(name, beschr, aktionen);
                formState.editAktionIndex = idx;
                zeigeAktionenEditorPanel(aktionen.get(idx));
            });
            ctrl.setOnAnlegen((name, beschr, aktionen) -> {
                model.addSzenario(name, beschr, aktionenListZuMap(aktionen));
                formState.zuruecksetzen();
                nachModelAenderung();
                zeigeSzenarienPanel();
            });
            ctrl.setOnAbbrechen(() -> {
                formState.zuruecksetzen();
                zeigeSzenarienPanel();
            });
        });
    }

    private void zeigeBearbeitenSzenarioPanel(final Szenario szenario) {
        formState.szenarioImBearbeitungsmodus = szenario;
        formState.name = szenario.getName();
        formState.beschreibung = szenario.getBeschreibung() != null ? szenario.getBeschreibung() : "";
        formState.aktionen.clear();
        szenario.getAenderungen().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> formState.aktionen.add(e.getValue()));
        formState.editAktionIndex = null;
        zeigeBearbeitenSzenarioPanelMitState();
    }

    private void zeigeBearbeitenSzenarioPanelMitState() {
        zeigePanelHelper("edit-szenario-view.fxml", loader -> {
            final BearbeitenSzenarioController ctrl = loader.getController();
            ctrl.setInitialState(formState.name, formState.beschreibung, new ArrayList<>(formState.aktionen));
            ctrl.setOnAktionHinzufuegen((name, beschr, aktionen) -> {
                formState.setze(name, beschr, aktionen);
                formState.editAktionIndex = null;
                zeigeAktionenEditorPanel(null);
            });
            ctrl.setOnAktionBearbeiten((name, beschr, aktionen, idx) -> {
                formState.setze(name, beschr, aktionen);
                formState.editAktionIndex = idx;
                zeigeAktionenEditorPanel(aktionen.get(idx));
            });
            ctrl.setOnSpeichern((name, beschr, aktionen) -> {
                model.updateSzenario(formState.szenarioImBearbeitungsmodus, name, beschr);
                ersetzeSzenarioAktionen(formState.szenarioImBearbeitungsmodus, aktionen);
                final Szenario gespeichert = formState.szenarioImBearbeitungsmodus;
                formState.zuruecksetzen();
                nachModelAenderung();
                zeigeSzenarioDetailPanel(gespeichert);
            });
            ctrl.setOnAbbrechen(() -> {
                final Szenario original = formState.szenarioImBearbeitungsmodus;
                formState.zuruecksetzen();
                zeigeSzenarioDetailPanel(original);
            });
            ctrl.setOnLoeschen(() -> {
                model.deleteSzenario(formState.szenarioImBearbeitungsmodus);
                formState.zuruecksetzen();
                nachModelAenderung();
                zeigeSzenarienPanel();
            });
        });
    }

    private void zeigeAktionenEditorPanel(final Szenario.Aenderung zuBearbeiten) {
        zeigePanelHelper("szenario-aktionen-editor-view.fxml", loader -> {
            final SzenarioAktionenEditorController ctrl = loader.getController();
            final String szenName = formState.szenarioImBearbeitungsmodus != null
                    ? formState.szenarioImBearbeitungsmodus.getName()
                    : formState.name;
            ctrl.setSzenarioName(szenName);
            ctrl.setGeraete(new ArrayList<>(model.getGeraete().values()));
            ctrl.setAenderung(zuBearbeiten);
            ctrl.setButtonText(zuBearbeiten == null ? "Aktion hinzufügen" : "Aktion speichern");
            ctrl.setOnSpeichern(aenderung -> {
                if (formState.editAktionIndex == null) {
                    formState.aktionen.add(aenderung);
                } else {
                    formState.aktionen.set(formState.editAktionIndex, aenderung);
                }
                if (formState.szenarioImBearbeitungsmodus != null) {
                    zeigeBearbeitenSzenarioPanelMitState();
                } else {
                    zeigeNeuesSzenarioPanelMitState();
                }
            });
            ctrl.setOnAbbrechen(() -> {
                if (formState.szenarioImBearbeitungsmodus != null) {
                    zeigeBearbeitenSzenarioPanelMitState();
                } else {
                    zeigeNeuesSzenarioPanelMitState();
                }
            });
        });
    }

    private void ersetzeSzenarioAktionen(final Szenario szenario, final List<Szenario.Aenderung> neueAktionen) {
        new ArrayList<>(szenario.getAenderungen().keySet())
                .forEach(pos -> model.deleteSzenarioAktion(szenario, pos));
        for (int i = 0; i < neueAktionen.size(); i++) {
            model.addSzenarioAktion(szenario, neueAktionen.get(i), i + 1);
        }
    }

    private Map<Integer, Szenario.Aenderung> aktionenListZuMap(final List<Szenario.Aenderung> aktionen) {
        final Map<Integer, Szenario.Aenderung> map = new LinkedHashMap<>();
        for (int i = 0; i < aktionen.size(); i++) {
            map.put(i + 1, aktionen.get(i));
        }
        return map;
    }

    private void updateStatusLog() {
        try {
            view.getStatusLogView().addMeldungen(
                    model.getStatusbereich().getNewMessages(view.getStatusLogView().getLetzteStatusMeldungsId()));
        } catch (MessageMissingException e) {
            StatusLog.addError(e.getMessage(), e);
        }
    }
}
