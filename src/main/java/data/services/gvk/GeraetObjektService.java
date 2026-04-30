package data.services.gvk;

import data.models.fachobjekte.Geraet;
import data.services.datenServices.DataAccess;
import data.services.datenServices.GeraetDataService;
import jakarta.inject.Singleton;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class GeraetObjektService {
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
            instance = new GeraetObjektService(GeraetDataService.getInstance(),new HashMap<>());
            dataAccess.mapAllGeraete(RaumObjektService.getInstance().getRaumMap(), instance.getGeraetMap());
        }
        return instance;
    }

    public Map<UUID, Geraet> getGeraetMap() {
        return geraetMap;
    }
}
