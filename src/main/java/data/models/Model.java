package data.models;

import data.models.ansichten.Statusbereich;
import data.models.ansichten.Uebersicht;
import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import data.services.datenServices.DataAccess;
import data.services.objektServices.GeraetObjektService;
import data.services.objektServices.RaumObjektService;
import data.services.objektServices.SzenarioObjektService;
import util.DoubleMap;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Model {
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
        Map<UUID, Raum> raumMap = null;
        Map<UUID, Geraet> geraetMap = null;
        Map<UUID, Szenario> szenarioMap = null;
        try {
            raumMap = RaumObjektService.getInstance().getAllRaeume();
            geraetMap = GeraetObjektService.getInstance().getAllGeraete(raumMap);
            szenarioMap = SzenarioObjektService.getInstance().getAllSzenarien(geraetMap);
        } catch (final SQLException e) {
            //TODO umbauen
            StatusLog.addHinweis("Fehler");
        }
        this.daten = new Daten(raumMap, geraetMap, szenarioMap);
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

    public record Daten(Map<UUID, Raum> raumMap, Map<UUID, Geraet> geraetMap, Map<UUID, Szenario> szenarioMap) {
    }
}
