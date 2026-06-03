package controller;

import data.models.fachobjekte.Geraet;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import userInterface.GeraetListCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class GeraeteController {

    @FXML private ListView<Geraet> geraetListView;

    private final ObservableList<Geraet> geraete = FXCollections.observableArrayList();
    private final Map<UUID, BooleanProperty> selektierteGeraete = new HashMap<>();

    private Consumer<List<UUID>> onAuswahlLoeschen;
    private Consumer<Geraet> onGeraetOeffnen;
    private Runnable onNeuenGeraetAnlegenRequested;

    public void setGeraete(final List<Geraet> geraete) {
        selektierteGeraete.clear();
        geraete.forEach(g -> selektierteGeraete.put(g.getId(), new SimpleBooleanProperty(false)));
        this.geraete.setAll(geraete);
    }

    public void setOnAuswahlLoeschen(final Consumer<List<UUID>> onAuswahlLoeschen) {
        this.onAuswahlLoeschen = onAuswahlLoeschen;
    }

    public void setOnGeraetOeffnen(final Consumer<Geraet> onGeraetOeffnen) {
        this.onGeraetOeffnen = onGeraetOeffnen;
    }

    public void setOnNeuenGeraetAnlegenRequested(final Runnable onNeuenGeraetAnlegenRequested) {
        this.onNeuenGeraetAnlegenRequested = onNeuenGeraetAnlegenRequested;
    }

    @FXML
    public void initialize() {
        geraetListView.setItems(geraete);
        geraetListView.setCellFactory(_ -> new GeraetListCell(
                this::isSelektiert,
                this::setSelektiert,
                this::geraetOeffnen));
    }

    @FXML
    private void onNeuenGeraetAnlegen() {
        if (onNeuenGeraetAnlegenRequested != null) onNeuenGeraetAnlegenRequested.run();
    }

    @FXML
    private void onAuswahlLoeschen() {
        if (onAuswahlLoeschen != null) onAuswahlLoeschen.accept(getSelektierteIds());
    }

    private void geraetOeffnen(final Geraet geraet) {
        if (onGeraetOeffnen != null) onGeraetOeffnen.accept(geraet);
    }

    private boolean isSelektiert(final UUID id) {
        final BooleanProperty prop = selektierteGeraete.get(id);
        return prop != null && prop.get();
    }

    private void setSelektiert(final UUID id, final boolean selektiert) {
        selektierteGeraete.computeIfAbsent(id, k -> new SimpleBooleanProperty()).set(selektiert);
    }

    private List<UUID> getSelektierteIds() {
        final List<UUID> result = new ArrayList<>();
        geraete.forEach(g -> {
            if (isSelektiert(g.getId())) result.add(g.getId());
        });
        return result;
    }
}
