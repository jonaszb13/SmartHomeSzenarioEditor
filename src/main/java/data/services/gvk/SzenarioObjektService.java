package data.services.gvk;

import data.models.fachobjekte.Szenario;
import data.services.datenServices.DataAccess;
import data.services.datenServices.SzenarioDataService;
import jakarta.inject.Singleton;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public final class SzenarioObjektService {
    private static SzenarioObjektService instance;
    private final SzenarioDataService dataService;
    private final Map<UUID, Szenario> szenarioMap;

    private SzenarioObjektService(SzenarioDataService dataService, Map<UUID, Szenario> szenarioMap) {
       this.dataService = dataService;
       this.szenarioMap = szenarioMap;
    }

    public static SzenarioObjektService getInstance() throws SQLException {
        if (instance == null) {
            DataAccess dataAccess= DataAccess.getInstance();
            instance = new SzenarioObjektService(SzenarioDataService.getInstance(dataAccess),new HashMap<>());
            dataAccess.mapAllSzenarien(GeraetObjektService.getInstance().getGeraetMap(), instance.szenarioMap);
        }
        return instance;
    }

    public Map<UUID, Szenario> getSzenarioMap() {
        return szenarioMap;
    }
}
