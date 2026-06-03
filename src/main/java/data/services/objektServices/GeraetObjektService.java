package data.services.objektServices;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.GeraetFactory;
import data.models.fachobjekte.Raum;
import data.services.datenServices.GeraetDataService;
import jakarta.inject.Singleton;
import util.statusmeldungen.StatusLog;

import javax.sql.rowset.CachedRowSet;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public final class GeraetObjektService {
    private static GeraetObjektService instance;
    private final GeraetDataService geraetDataService;
    private Map<UUID, Geraet> geraetMap;

    private GeraetObjektService(final GeraetDataService geraetDataService) {
        this.geraetDataService = geraetDataService;
    }

    public static GeraetObjektService getInstance() throws SQLException {
        if (instance == null) {
            instance = new GeraetObjektService(GeraetDataService.getInstance());
        }
        return instance;
    }

    public Map<UUID, Geraet> getGeraetMap() {
        return geraetMap;
    }

    public Map<UUID, Geraet> getAllGeraete(final Map<UUID, Raum> raumMap) throws SQLException {
        StatusLog.addHinweis("Beginne GeräteMap zu laden");
        Map<UUID, Geraet> localGeraetMap = new HashMap<>();
        //TODO QUESTION: Sollen geräte ohne Attribute geladen werden?
        CachedRowSet crs = geraetDataService.getAllGeraete();
        Map<String, String> attributeHashMap = new HashMap<>();
        UUID lastId = null;
        Geraet aktuellesGeraet = null;
        boolean erstesMal = true;
        while (crs.next()) {
            final UUID id = UUID.fromString(crs.getString("id"));
            if (!id.equals(lastId)) {
                if (erstesMal) erstesMal = false;
                else {
                    aktuellesGeraet.setValues(attributeHashMap);
                    attributeHashMap = new HashMap<>();
                }
                final UUID raum = UUID.fromString(crs.getString("raum"));
                try {
                    aktuellesGeraet = GeraetFactory.getInstance().createGeraet(id, crs.getString("name"),
                            raumMap.get(raum), crs.getString("art"));
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    StatusLog.addError("Bei der dynamischen Erstellung eines Geräts ist ein Fehler aufgetreten", e);
                    //TODO was mit Null tun?
                }
                attributeHashMap.put(crs.getString("schluessel"), crs.getString("wert"));
                localGeraetMap.put(id, aktuellesGeraet);
                raumMap.get(raum).getGeraete().add(aktuellesGeraet);
                lastId = id;
            }
            attributeHashMap.put(crs.getString("schluessel"), crs.getString("wert"));
        }
        if (aktuellesGeraet != null) aktuellesGeraet.setValues(attributeHashMap);
        StatusLog.addHinweis("GeräteMap erfolgreich geladen");
        geraetMap = localGeraetMap;
        return localGeraetMap;
    }

    public boolean addGeraet(final String name, final String art, final Raum raum, final Map<String, String> attributeMap) {
        boolean erfolgreich = false;
        UUID id = UUID.randomUUID();
        while (geraetMap.containsKey(id)) {
            id = UUID.randomUUID();
        }
        try {
            final Geraet geraet = GeraetFactory.getInstance().createGeraet(id, name, raum, art);
            if (!geraet.isGueltigeAttribute(attributeMap)) {
                StatusLog.addError("Das neue Gerät konnte nicht angelegt werden");
                return erfolgreich;
            }
            if (geraetDataService.addGeraet(geraet, art, attributeMap)) {
                geraet.setValues(attributeMap);
                geraetMap.put(id, geraet);
                StatusLog.addHinweis("Neues Gerät hinzugefügt. ID: " + geraet.getId());
                erfolgreich = true;
            } else {
                StatusLog.addError("Gerät konnte nicht vollständig angelegt werden.");
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            StatusLog.addError(e);
        }
        return erfolgreich;
    }

    public boolean deleteGeraet(final UUID id) {
        boolean erfolgreich = false;
        if (geraetDataService.deleteGeraet(geraetMap.get(id))) {
            geraetMap.remove(id);
            StatusLog.addHinweis("Gerät mit id " + id + " erfolgreich gelöscht");
            erfolgreich = true;
        } else {
            StatusLog.addError("Gerät mit id " + id + " konnte nicht gelöscht werden");
        }
        return erfolgreich;
    }

    public boolean updateGeraetName(final Geraet geraet, final String newName) {
        boolean erfolgreich = false;
        if (geraetDataService.updateGeraetName(geraet, newName)) {
            geraetMap.get(geraet.getId()).setName(newName);
            StatusLog.addHinweis("Name des Geräts " + geraet.getId() + " erfolgreich zu " + newName + " geändert");
            erfolgreich = true;
        } else {
            StatusLog.addError("Name des Geräts " + geraet.getId() + " konnte nicht geändert werden");
        }
        return erfolgreich;
    }

    public boolean updateGeraetRaum(final Geraet geraet, final Raum raum) {
        boolean erfolgreich = false;
        if (geraetDataService.updateGeraetRaum(geraet, raum)) {
            geraetMap.get(geraet.getId()).setRaum(raum);
            StatusLog.addHinweis("Raum des Geräts " + geraet.getId() + " erfolgreich zu " + raum.getName() + " geändert");
            erfolgreich = true;
        } else {
            StatusLog.addError("Raum des Geräts " + geraet.getId() + " konnte nicht geändert werden");
        }
        return erfolgreich;
    }

    public boolean updateGeraetWerte(final Geraet geraet, final Map<String, String> attributeMap) {
        boolean erfolgreich = false;
        if (!geraet.isGueltigeAttribute(attributeMap)) {
           StatusLog.addError("Attribute des Geräts " + geraet.getName() + " konnten nicht aktualisiert werden");
           return erfolgreich;
        }
        if (geraetDataService.updateGeraetWerte(geraet, attributeMap)) {
            geraet.setValues(attributeMap);
            StatusLog.addHinweis("Attribute des Geräts " + geraet.getName() + " wurden aktualisiert");
            erfolgreich = true;
        } else {
            StatusLog.addError("Attribute des Geräts " + geraet.getName() + " konnten nicht aktualisiert werden");
        }
        return erfolgreich;
    }

}
