package data.models;

import data.models.ansichten.Statusbereich;
import data.models.ansichten.Uebersicht;
import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import data.services.datenServices.DataAccess;
import util.DoubleMap;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Model {
    private final Uebersicht uebersicht;
    private final Statusbereich statusbereich;
    private final Daten daten;
    private static Model instance;

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    private Model() {
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

    public void load() throws SQLException {
        DataAccess dataAccess = DataAccess.getInstance();
        dataAccess.mapAllRaeume(getDaten().raumMap);
        dataAccess.mapAllGeraete(getDaten().raumMap, getDaten().geraetMap);
        dataAccess.mapAllSzenarien(getDaten().geraetMap, getDaten().szenarioMap);
    }

    public record Daten(Map<UUID, Raum> raumMap, Map<UUID, Geraet> geraetMap, Map<UUID, Szenario> szenarioMap) {
    }
}
