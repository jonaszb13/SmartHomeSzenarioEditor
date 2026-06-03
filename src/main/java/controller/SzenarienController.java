package controller;

import data.models.fachobjekte.Szenario;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import userInterface.SzenarioListCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SzenarienController {

    @FXML
    private ListView<Szenario> szenarioListView;

    private final ObservableList<Szenario> szenarien = FXCollections.observableArrayList();
    private final Map<UUID, BooleanProperty> selektierteSzenarien = new HashMap<>();

    private Consumer<List<UUID>> onAuswahlLoeschen;
    private Consumer<Szenario> onSzenarioOeffnen;
    private Consumer<Szenario> onSzenarioAusfuehren;
    private Runnable onNeuesSzenarioAnlegenRequested;

    public void setSzenarien(final List<Szenario> szenarien) {
        selektierteSzenarien.clear();
        szenarien.forEach(szenario -> selektierteSzenarien.put(szenario.getId(), new SimpleBooleanProperty(false)));
        this.szenarien.setAll(szenarien);
    }

    public void setOnAuswahlLoeschen(final Consumer<List<UUID>> onAuswahlLoeschen) {
        this.onAuswahlLoeschen = onAuswahlLoeschen;
    }

    public void setOnSzenarioOeffnen(final Consumer<Szenario> onSzenarioOeffnen) {
        this.onSzenarioOeffnen = onSzenarioOeffnen;
    }

    public void setOnSzenarioAusfuehren(final Consumer<Szenario> onSzenarioAusfuehren) {
        this.onSzenarioAusfuehren = onSzenarioAusfuehren;
    }

    public void setOnNeuesSzenarioAnlegenRequested(final Runnable onNeuesSzenarioAnlegenRequested) {
        this.onNeuesSzenarioAnlegenRequested = onNeuesSzenarioAnlegenRequested;
    }

    @FXML
    public void initialize() {
        szenarioListView.setItems(szenarien);
        szenarioListView.setCellFactory(_ -> new SzenarioListCell(
                this::isSelektiert,
                this::setSelektiert,
                this::szenarioOeffnen,
                this::szenarioAusfuehren));
    }

    @FXML
    private void onNeuesSzenarioAnlegen() {
        if (onNeuesSzenarioAnlegenRequested != null) onNeuesSzenarioAnlegenRequested.run();
    }

    @FXML
    private void onAuswahlLoeschen() {
        if (onAuswahlLoeschen != null) onAuswahlLoeschen.accept(getSelektierteIds());
    }

    private void szenarioOeffnen(final Szenario szenario) {
        if (onSzenarioOeffnen != null) onSzenarioOeffnen.accept(szenario);
    }

    private void szenarioAusfuehren(final Szenario szenario) {
        if (onSzenarioAusfuehren != null) onSzenarioAusfuehren.accept(szenario);
    }

    private boolean isSelektiert(final UUID id) {
        final BooleanProperty prop = selektierteSzenarien.get(id);
        return prop != null && prop.get();
    }

    private void setSelektiert(final UUID id, final boolean selektiert) {
        selektierteSzenarien.computeIfAbsent(id, k -> new SimpleBooleanProperty()).set(selektiert);
    }

    private List<UUID> getSelektierteIds() {
        final List<UUID> result = new ArrayList<>();
        szenarien.forEach(s -> {
            if (isSelektiert(s.getId())) result.add(s.getId());
        });
        return result;
    }
}
