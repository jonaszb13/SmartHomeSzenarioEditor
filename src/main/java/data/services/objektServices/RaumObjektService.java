package data.services.objektServices;

import data.models.fachobjekte.Raum;
import data.services.datenServices.RaumDataService;
import jakarta.inject.Singleton;
import javafx.scene.control.TreeItem;
import util.DoubleMap;
import util.statusmeldungen.StatusLog;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public final class RaumObjektService {
    private static RaumObjektService instance;
    private final RaumDataService raumDataService;
    private Map<UUID, Raum> raumMap;
    private DoubleMap<UUID, TreeItem<String>> raumTreeMap;

    private RaumObjektService(final RaumDataService raumDataService, final DoubleMap<UUID, TreeItem<String>> raumTreeMap) {
        this.raumDataService = raumDataService;
        this.raumTreeMap = raumTreeMap;
    }

    public static RaumObjektService getInstance() throws SQLException {
        if (instance == null) {
            instance = new RaumObjektService(RaumDataService.getInstance(), new DoubleMap<>());
        }
        return instance;
    }

    public Map<UUID, Raum> getRaumMap() {
        return raumMap;
    }

    public DoubleMap<UUID, TreeItem<String>> getRaumTreeMap() {
        return raumTreeMap;
    }

    public Map<UUID, Raum> getAllRaeume() throws SQLException {
        StatusLog.addHinweis("Beginne RäumeMap zu laden");
        Map<UUID, Raum> localRaumMap = new HashMap<>();
        CachedRowSet crs = raumDataService.getAllRaeume();
        while (crs.next()) {
            final UUID id = UUID.fromString(crs.getString("id"));
            final String name = crs.getString("name");
            localRaumMap.put(id, new Raum(id, name));
        }
        StatusLog.addHinweis("RäumeMap erfolgreich geladen");
        //TODO Abändern?
        this.raumMap = localRaumMap;
        return localRaumMap;
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
