package data.services.gvk;

import data.models.fachobjekte.Raum;
import data.services.datenServices.DataAccess;
import data.services.datenServices.RaumDataService;
import jakarta.inject.Singleton;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public final class RaumObjektService {
    private static RaumObjektService instance;
    private final RaumDataService raumDataService;
    private final Map<UUID, Raum> raumMap;

    private RaumObjektService(RaumDataService raumDataService, Map<UUID, Raum> raumMap) {
        this.raumDataService = raumDataService;
        this.raumMap = raumMap;
    }

    public static RaumObjektService getInstance() throws SQLException {
        if (instance == null) {
            DataAccess dataAccess = DataAccess.getInstance();
            instance = new RaumObjektService(RaumDataService.getInstance(dataAccess), new HashMap<>());
            dataAccess.mapAllRaeume(instance.getRaumMap());
        }
        return instance;
    }

    public Map<UUID, Raum> getRaumMap() {
        return raumMap;
    }

    public boolean addRaum(String name) {
        UUID uuid = UUID.randomUUID();
        while (raumMap.containsKey(uuid)) {
            uuid = UUID.randomUUID();
        }
        Raum raum = new Raum(uuid, name);
        if (raumDataService.addRaum(raum)) {
            raumMap.put(uuid, raum);
            StatusLog.addHinweis("Raum angelegt: " + raum.getName() + " ID: " + uuid);
            return true;
        } else {
            StatusLog.addError("Raum konnte aufgrund eines SQL Fehlers nicht angelegt werden");
            return false;
        }
    }

    public boolean updateRaum(Raum raum, String neuerName) {
        if (raumDataService.updateRaumName(raum.getId(),neuerName)) {
            raumMap.get(raum.getId()).setName(neuerName);
            StatusLog.addHinweis("Name des Raums " + raum.getId() + " wurde auf " + neuerName + " geändert.");
            return true;
        } else {
            StatusLog.addError("Name des Raums " + raum.getId() + " konnte nicht geändert werden");
            return false;
        }
    }

    public boolean deleteRaum(Raum raum) {
        if (raumDataService.deleteRaum(raum.getId())) {
            raumMap.remove(raum.getId());
            StatusLog.addHinweis("Raum " + raum.getId() + " wurde gelöscht.");
            return true;
        } else {
            StatusLog.addError("Raum " + raum.getId() + " konnte nicht gelöscht werden");
            return false;
        }
    }

}
