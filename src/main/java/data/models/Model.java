package data.models;

import data.models.ansichten.Statusbereich;
import data.models.ansichten.Uebersicht;
import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import util.DoubleMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Model {
    private final Uebersicht uebersicht;
    private final Statusbereich statusbereich;
    private final Daten daten;

    public Model() {
        this.uebersicht = new Uebersicht(new DoubleMap<>(), new DoubleMap<>(), new DoubleMap<>());
        this.statusbereich = new Statusbereich();
        this.daten = new Daten(new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public Uebersicht getUebersicht() {
        return uebersicht;
    }

    public Statusbereich getStatusbereich() {
        return statusbereich;
    }

    public Daten getDaten() {
        return daten;
    }

    public void load() {

    }

    public record Daten (Map<UUID, Raum> raumMap, Map<UUID, Geraet> geraetMap, Map<UUID, Szenario> szenarioMap){}
}
