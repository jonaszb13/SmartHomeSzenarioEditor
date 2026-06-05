package data.models;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.GeraetFactory;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import data.services.objektservices.GeraetObjektService;
import data.services.objektservices.RaumObjektService;
import data.services.objektservices.SzenarioAusfuehrungsService;
import data.services.objektservices.SzenarioObjektService;
import util.customexceptions.NoGeraetProvidedException;
import util.statusmeldungen.StatusLog;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class Model {
    private static Model instance;
    private final StatusLog statusbereich;
    private RaumObjektService raumObjektService;
    private GeraetObjektService geraetObjektService;
    private SzenarioObjektService szenarioObjektService;
    private SzenarioAusfuehrungsService szenarioAktivationService;

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    private Model() {
        try {
            raumObjektService = RaumObjektService.getInstance();
            geraetObjektService = GeraetObjektService.getInstance();
            szenarioObjektService = SzenarioObjektService.getInstance();
            szenarioAktivationService = SzenarioAusfuehrungsService.getInstance();
            final Map<UUID, Raum> raumMap = raumObjektService.getAllRaeume();
            final Map<UUID, Geraet> geraetMap = geraetObjektService.getAllGeraete(raumMap);
            szenarioObjektService.ladeAlleSzenarien(geraetMap);
        } catch (SQLException eSQL) {
            StatusLog.addError("Das Model konnte nicht geladen werden: ", eSQL);
        }
        this.statusbereich = StatusLog.getInstance();
    }

    public StatusLog getStatusbereich() {
        return statusbereich;
    }

    // --- Räume ---

    public Map<UUID, Raum> getRaumMap() {
        return raumObjektService.getRaumMap();
    }

    public Raum getRaum(final UUID id) {
        return raumObjektService.getRaumMap().get(id);
    }

    public boolean addRaum(final String name) {
        return raumObjektService.addRaum(name);
    }

    public boolean updateRaum(final Raum raum, final String neuerName) {
        return raumObjektService.updateRaum(raum, neuerName);
    }

    public boolean deleteRaum(final UUID id) {
        return raumObjektService.deleteRaum(id);
    }

    // --- Geräte ---

    public Set<String> getGeraeteTypen() {
        try {
            return GeraetFactory.getInstance().getGeraeteTypen();
        } catch (NoGeraetProvidedException e) {
            StatusLog.addError("Gerätetypen konnten nicht geladen werden", e);
            return Set.of();
        }
    }

    public Map<String, Class<?>> getAttributTypenFuerGeraetTyp(final String typ) {
        try {
            final Geraet tempGeraet = GeraetFactory.getInstance().createGeraet(UUID.randomUUID(), "", null, typ);
            return tempGeraet != null ? tempGeraet.getAttributTypen() : Map.of();
        } catch (NoGeraetProvidedException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException | IllegalAccessException e) {
            StatusLog.addError("Attributtypen für Gerätetyp konnten nicht geladen werden", e);
            return Map.of();
        }
    }

    public Map<UUID, Geraet> getGeraete() {
        return geraetObjektService.getGeraetMap();
    }

    public Geraet getGeraet(final UUID id) {
        return geraetObjektService.getGeraetMap().get(id);
    }

    public boolean addGeraet(final String name, final String art, final Raum raum, final Map<String, String> attributeMap) {
        return geraetObjektService.addGeraet(name, art, raum, attributeMap);
    }

    public boolean deleteGeraet(final UUID id) {
        return geraetObjektService.deleteGeraet(id);
    }

    public boolean updateGeraetName(final Geraet geraet, final String name) {
        if (geraet.getName().equals(name)) return false;
        return geraetObjektService.updateGeraetName(geraet, name);
    }

    public boolean updateGeraetRaum(final Geraet geraet, final Raum raum) {
        if (geraet.getRaum().equals(raum)) return false;
        return geraetObjektService.updateGeraetRaum(geraet, raum);
    }

    public boolean updateGeraetWerte(final Geraet geraet, final Map<String, String> attributeMap) {
        return geraetObjektService.updateGeraetWerte(geraet, attributeMap);
    }

    // --- Szenarien ---

    public Map<UUID, Szenario> getSzenarioMap() {
        return szenarioObjektService.getSzenarioMap();
    }

    public Szenario getSzenario(final UUID id) {
        return szenarioObjektService.getSzenarioMap().get(id);
    }

    public boolean addSzenario(final String name, final String beschreibung, final Map<Integer, Szenario.Aenderung> aenderungen) {
        return szenarioObjektService.addSzenario(name, beschreibung, aenderungen);
    }

    public boolean deleteSzenario(final Szenario szenario) {
        return szenarioObjektService.deleteSzenario(szenario);
    }

    public boolean updateSzenario(final Szenario szenario, final String name, final String beschreibung) {
        return szenarioObjektService.updateSzenario(szenario, name, beschreibung);
    }


    public boolean addSzenarioAktion(final Szenario szenario, final Szenario.Aenderung aenderung, final int position) {
        return szenarioObjektService.addSzenarioInhalt(szenario, aenderung, position);
    }

    public boolean deleteSzenarioAktion(final Szenario szenario, final int position) {
        return szenarioObjektService.deleteSzenarioInhalt(szenario, position);
    }

    public boolean fuehreSzenarioAus(final Szenario szenario) {
        return szenarioAktivationService.fuehreSzenarioAus(szenario);
    }

    public void reload() {
        try {
            Map<UUID, Raum> raumMap = raumObjektService.getAllRaeume();
            Map<UUID, Geraet> geraetMap = geraetObjektService.getAllGeraete(raumMap);
            szenarioObjektService.ladeAlleSzenarien(geraetMap);
        } catch (SQLException eSQL) {
            StatusLog.addError("Das Model konnte nicht neu geladen werden: ", eSQL);
        }
    }

}
