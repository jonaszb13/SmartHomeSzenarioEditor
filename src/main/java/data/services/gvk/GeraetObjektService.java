package data.services.gvk;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.GeraetFactory;
import data.models.fachobjekte.Raum;
import data.services.datenServices.DataAccess;
import data.services.datenServices.GeraetDataService;
import jakarta.inject.Singleton;
import util.statusmeldungen.StatusLog;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public final class GeraetObjektService {
    private static GeraetObjektService instance;
    private final GeraetDataService geraetDataService;
    private final Map<UUID, Geraet> geraetMap;

    private GeraetObjektService(GeraetDataService geraetDataService, Map<UUID, Geraet> geraetMap) {
        this.geraetDataService = geraetDataService;
        this.geraetMap = geraetMap;
    }

    public static GeraetObjektService getInstance() throws SQLException {
        if (instance == null) {
            DataAccess dataAccess = DataAccess.getInstance();
            instance = new GeraetObjektService(GeraetDataService.getInstance(), new HashMap<>());
            dataAccess.mapAllGeraete(RaumObjektService.getInstance().getRaumMap(), instance.getGeraetMap());
        }
        return instance;
    }

    public Map<UUID, Geraet> getGeraetMap() {
        return geraetMap;
    }

    public boolean addGeraet(String name, String art, Raum raum, Map<String, String> attributeMap) {
        boolean erfolgreich = false;
        UUID id = UUID.randomUUID();
        while (geraetMap.containsKey(id)) {
            id = UUID.randomUUID();
        }
        try {
            Geraet geraet = GeraetFactory.getInstance().createGeraet(id, name, raum, art);
            if (geraetDataService.addGeraet(geraet, art, attributeMap)) {
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

    public boolean deleteGeraet(UUID id) {
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

    public boolean updateGeraetName(Geraet geraet, String newName) {
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

    public boolean updateGeraetRaum(Geraet geraet, Raum raum) {
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

    public boolean updateGeraetWerte(Geraet geraet, Map<String, String> attributeMap) {
        boolean erfolgreich = false;
        if (geraetDataService.updateGeraetWerte(geraet, attributeMap)) {
            geraet.setValues(attributeMap);
            StatusLog.addHinweis("Attribute des Geräts " + geraet.getId() + " wurden aktualisiert");
            erfolgreich = true;
        } else {
            StatusLog.addError("Attribute des Geräts " + geraet.getId() + " konnten nicht aktualisiert werden");
        }
        return erfolgreich;
    }

}
