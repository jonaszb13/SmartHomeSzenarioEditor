package data.services.gvk;

import data.models.fachobjekte.Raum;
import data.services.datenServices.DataAccess;
import data.services.datenServices.RaumDataService;
import jakarta.inject.Singleton;

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


}
