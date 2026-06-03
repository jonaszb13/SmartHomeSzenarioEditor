package data.models;

import data.models.ansichten.Statusbereich;
import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.GeraetFactory;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import data.services.objektServices.GeraetObjektService;
import data.services.objektServices.RaumObjektService;
import data.services.objektServices.SzenarioAktivationService;
import data.services.objektServices.SzenarioObjektService;
import util.customExceptions.NoGeraetProvidedException;
import util.statusmeldungen.StatusLog;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class Model {
    private static Model instance;
    private final Statusbereich statusbereich;
    private RaumObjektService raumObjektService = null;
    private GeraetObjektService geraetObjektService = null;
    private SzenarioObjektService szenarioObjektService = null;
    private SzenarioAktivationService szenarioAktivationService = null;

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
            szenarioAktivationService = SzenarioAktivationService.getInstance();
            Map<UUID, Raum> raumMap = raumObjektService.getAllRaeume();
            Map<UUID, Geraet> geraetMap = geraetObjektService.getAllGeraete(raumMap);
            szenarioObjektService.getAllSzenarien(geraetMap);
        } catch (SQLException eSQL) {
            StatusLog.addError("Das Model konnte nicht geladen werden: ", eSQL);
        }
        this.statusbereich = new Statusbereich();
    }

    public Statusbereich getStatusbereich() {
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
        return geraetObjektService.updateGeraetName(geraet, name);
    }

    public boolean updateGeraetRaum(final Geraet geraet, final Raum raum) {
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

    public boolean updateSzenarioStatus(final Szenario szenario, final boolean status) {
        return szenarioObjektService.updateSzenarioStatus(szenario, status);
    }

    public boolean addSzenarioAktion(final Szenario szenario, final Szenario.Aenderung aenderung, final int position) {
        return szenarioObjektService.addSzenarioInhalt(szenario, aenderung, position);
    }

    public boolean updateSzenarioAktion(final Szenario szenario, final int position, final Geraet geraet,
                                        final String beschreibung, final String schluessel, final String wert) {
        return szenarioObjektService.alterSzenarioInhalt(szenario, position, geraet, beschreibung, schluessel, wert);
    }

    public boolean deleteSzenarioAktion(final Szenario szenario, final int position) {
        return szenarioObjektService.deleteSzenarioInhalt(szenario, position);
    }

    public Szenario.Aenderung createAenderung(final Geraet geraet, final String beschreibung,
                                              final String schluessel, final String wert) {
        return szenarioObjektService.getAenderung(geraet, beschreibung, schluessel, wert);
    }

    public boolean aktiviereSzenario(final Szenario szenario) {
        return szenarioAktivationService.aktiviereSzenario(szenario);
    }

    public boolean deaktiviereSzenario(final Szenario szenario) {
        return szenarioAktivationService.deaktiviereSzenario(szenario);
    }

}
