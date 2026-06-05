package controller;

import data.models.fachobjekte.Raum;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import user_interface.RaumListCell;

import java.util.*;
import java.util.function.Consumer;

public class RaeumeController {

    @FXML private ListView<Raum> raumListView;

    private final ObservableList<Raum> raeume = FXCollections.observableArrayList();
    private final Map<UUID, BooleanProperty> selektierteRaeume = new HashMap<>();

    private Consumer<List<UUID>> onAuswahlLoeschen;
    private Consumer<Raum> onRaumOeffnen;
    private Runnable onNeuenRaumAnlegenRequested;

    public void setRaeume(final List<Raum> raeume) {
        selektierteRaeume.clear();
        raeume.forEach(raum -> selektierteRaeume.put(raum.getId(), new SimpleBooleanProperty(false)));
        this.raeume.setAll(raeume);
    }

    public void setOnAuswahlLoeschen(final Consumer<List<UUID>> onAuswahlLoeschen) {
        this.onAuswahlLoeschen = onAuswahlLoeschen;
    }

    public void setOnRaumOeffnen(final Consumer<Raum> onRaumOeffnen) {
        this.onRaumOeffnen = onRaumOeffnen;
    }

    public void setOnNeuenRaumAnlegenRequested(final Runnable onNeuenRaumAnlegenRequested) {
        this.onNeuenRaumAnlegenRequested = onNeuenRaumAnlegenRequested;
    }

    @FXML
    public void initialize() {
        raumListView.setItems(raeume);
        raumListView.setCellFactory(_ -> new RaumListCell(
                this::isSelektiert,
                this::setSelektiert,
                this::raumOeffnen));
    }

    @FXML
    private void onNeuenRaumAnlegen() {
        if (onNeuenRaumAnlegenRequested != null) onNeuenRaumAnlegenRequested.run();
    }

    @FXML
    private void onAuswahlLoeschen() {
        if (onAuswahlLoeschen != null) {
            onAuswahlLoeschen.accept(getSelektierteIds());
        }
    }

    private void raumOeffnen(final Raum raum) {
        if (onRaumOeffnen != null) {
            onRaumOeffnen.accept(raum);
        }
    }

    private boolean isSelektiert(final UUID id) {
        final BooleanProperty prop = selektierteRaeume.get(id);
        return prop != null && prop.get();
    }

    private void setSelektiert(final UUID id, final boolean selektiert) {
        selektierteRaeume.computeIfAbsent(id, k -> new SimpleBooleanProperty()).set(selektiert);
    }

    private List<UUID> getSelektierteIds() {
        final List<UUID> result = new ArrayList<>();
        raeume.forEach(raum -> {
            if (isSelektiert(raum.getId())) result.add(raum.getId());
        });
        return result;
    }
}
