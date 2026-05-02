package data.services.objektServices;

import data.models.fachobjekte.Raum;
import data.services.datenServices.DataAccess;
import data.services.datenServices.RaumDataService;
import jakarta.inject.Singleton;
import javafx.scene.control.TreeItem;
import util.DoubleMap;
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
    private final DoubleMap<UUID, TreeItem<String>> raumTreeMap;

    private RaumObjektService(final RaumDataService raumDataService, final Map<UUID, Raum> raumMap) {
        this.raumDataService = raumDataService;
        this.raumMap = raumMap;
        this.raumTreeMap = new DoubleMap<>();
    }

    public static RaumObjektService getInstance() throws SQLException {
        if (instance == null) {
            final DataAccess dataAccess = DataAccess.getInstance();
            instance = new RaumObjektService(RaumDataService.getInstance(dataAccess), new HashMap<>());
            dataAccess.mapAllRaeume(instance.getRaumMap());
        }
        return instance;
    }

    public Map<UUID, Raum> getRaumMap() {
        return raumMap;
    }

    public DoubleMap<UUID, TreeItem<String>> getRaumTreeMap() {
        return raumTreeMap;
    }

    public boolean addRaum(final String name) {
        boolean erfolgreich = false;
        UUID uuid = UUID.randomUUID();
        while (raumMap.containsKey(uuid)) {
            uuid = UUID.randomUUID();
        }
        final Raum raum = new Raum(uuid, name);
        if (raumDataService.addRaum(raum)) {
            raumMap.put(uuid, raum);
            raumTreeMap.put(uuid, new TreeItem<>(name));
            StatusLog.addHinweis("Raum angelegt: " + raum.getName() + " ID: " + uuid);
            erfolgreich = true;
        } else {
            StatusLog.addError("Raum konnte aufgrund eines SQL Fehlers nicht angelegt werden");
        }
        return erfolgreich;
    }

    public boolean updateRaum(final Raum raum, final String neuerName) {
        boolean erfolgreich = false;
        if (raumDataService.updateRaumName(raum.getId(), neuerName)) {
            raumMap.get(raum.getId()).setName(neuerName);
            StatusLog.addHinweis("Name des Raums " + raum.getId() + " wurde auf " + neuerName + " geändert.");
            erfolgreich = true;
        } else {
            StatusLog.addError("Name des Raums " + raum.getId() + " konnte nicht geändert werden");
        }
        return erfolgreich;
    }

    public boolean deleteRaum(final UUID id) {
        boolean erfolgreich = false;
        if (raumDataService.deleteRaum(id)) {
            raumMap.remove(id);
            raumTreeMap.removeByA(id);
            StatusLog.addHinweis("Raum " + id + " wurde gelöscht.");
            erfolgreich = true;
        } else {
            StatusLog.addError("Raum " + id + " konnte nicht gelöscht werden");
        }
        return erfolgreich;
    }

}
