package com.smarthome.controller;

import com.smarthome.model.entity.Raum;
import com.smarthome.model.service.RaumService;
import com.smarthome.util.StatusLog;
import com.smarthome.view.RaumListCell;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class RaumController {

    @FXML private ListView<Raum> raumListView;

    private final ObservableList<Raum> raeume = FXCollections.observableArrayList();
    private final Map<UUID, BooleanProperty> selektierteRaeume = new HashMap<>();

    private RaumService raumService;
    private Consumer<Pane> panelWechseln;

    public void setRaumService(final RaumService raumService) {
        this.raumService = raumService;
        ladeRaeume();
    }

    public void setPanelWechseln(final Consumer<Pane> panelWechseln) {
        this.panelWechseln = panelWechseln;
    }

    @FXML
    public void initialize() {
        raumListView.setItems(raeume);
        raumListView.setCellFactory(_ -> new RaumListCell(
                this::isSelektiert,
                this::setSelektiert,
                this::raumOeffnen,
                this::raumBearbeiten));
    }

    @FXML
    private void onNeuenRaumAnlegen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/ui/new-raum-view.fxml"));
            Pane formular = loader.load();
            NewRaumController newRaumController = loader.getController();
            newRaumController.setOnNeuenRaumAnlegen(name -> {
                raumService.addRaum(name);
                ladeRaeume();
                zeigeRaumListe();
            });
            newRaumController.setOnAbbrechen(this::zeigeRaumListe);
            panelWechseln.accept(formular);
        } catch (IOException e) {
            StatusLog.addError("Neuer-Raum-Formular konnte nicht geladen werden", e);
        }
    }

    @FXML
    private void onAuswahlLoeschen() {
        getSelektiert().forEach(raumService::deleteRaum);
        raeume.removeIf(r -> {
            BooleanProperty prop = selektierteRaeume.remove(r.getId());
            return prop != null && prop.get();
        });
    }

    private void raumOeffnen(final Raum raum) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/ui/info-raum-view.fxml"));
            Pane infoPane = loader.load();
            InfoRaumController infoController = loader.getController();
            infoController.setRaum(raum);
            infoController.setOnSchliessen(this::zeigeRaumListe);
            panelWechseln.accept(infoPane);
        } catch (IOException e) {
            StatusLog.addError("Info-Raum-Ansicht konnte nicht geladen werden", e);
        }
    }

    private void raumBearbeiten(final Raum raum) {
    }

    private void ladeRaeume() {
        List<Raum> raeumeListe = raumService.getAlleRaeume();
        selektierteRaeume.clear();
        raeumeListe.forEach(r -> selektierteRaeume.put(r.getId(), new SimpleBooleanProperty(false)));
        raeume.setAll(raeumeListe);
    }

    private boolean isSelektiert(final UUID id) {
        BooleanProperty prop = selektierteRaeume.get(id);
        return prop != null && prop.get();
    }

    private void setSelektiert(final UUID id, final boolean selektiert) {
        selektierteRaeume.computeIfAbsent(id, k -> new SimpleBooleanProperty()).set(selektiert);
    }

    private List<UUID> getSelektiert() {
        List<UUID> result = new ArrayList<>();
        raeume.forEach(raum -> {
            if (isSelektiert(raum.getId())) result.add(raum.getId());
        });
        return result;
    }

    private void zeigeRaumListe() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/ui/raum-view.fxml"));
            Pane raumListe = loader.load();
            RaumController controller = loader.getController();
            controller.setRaumService(raumService);
            controller.setPanelWechseln(panelWechseln);
            panelWechseln.accept(raumListe);
        } catch (IOException e) {
            StatusLog.addError("Raumliste konnte nicht geladen werden", e);
        }
    }
}