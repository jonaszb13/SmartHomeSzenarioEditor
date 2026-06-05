package unit.services.datenservices;

import data.models.fachobjekte.Raum;
import data.services.datenservices.DataAccess;
import data.services.datenservices.DatabaseCreationService;
import data.services.datenservices.RaumDataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RaumDataServiceTest {
    static final String RAUM_2 = "Raum 2";
    static final String UUID_1 = "9bf21849-af67-4c50-ba0d-6e991850ceb4";
    public static final String RAUM_1 = "Raum 1";
    private static DataAccess dataAccess;
    private static RaumDataService raumDataService;

    @BeforeAll
    static void setup() throws SQLException {
        DataAccess.setTest(true);
        DatabaseCreationService.createDatabase();
        dataAccess = DataAccess.getInstance();
        raumDataService = RaumDataService.getInstance();
    }

    @Test
    void testGetAllRaeume() throws Exception {
        Raum raum1 = new Raum(UUID.fromString(UUID_1), RAUM_1);
        Raum raum2 = new Raum(UUID.fromString("0d481ee5-8528-42e0-bf14-e224e3d84ab0"), RAUM_2);
        raumDataService.addRaum(raum1);
        raumDataService.addRaum(raum2);

        CachedRowSet crs = raumDataService.getAllRaeume();
        Map<UUID, String> result = new HashMap<>();
        while (crs.next()) {
            result.put(UUID.fromString(crs.getString("id")), crs.getString("name"));
        }

        assertEquals(2, result.size());
        assertEquals(RAUM_1, result.get(raum1.getId()));
        assertEquals(RAUM_2, result.get(raum2.getId()));
    }

    @Test
    void testAddRaum() throws Exception {
        Raum raum1 = new Raum(UUID.fromString(UUID_1), RAUM_1);
        Raum raum2 = new Raum(UUID.fromString("0d481ee5-8528-42e0-bf14-e224e3d84ab0"), RAUM_2);
        raumDataService.addRaum(raum1);
        raumDataService.addRaum(raum2);

        //language=SQL
        CachedRowSet crs = dataAccess.getData("""
                SELECT COUNT(*) FROM RAEUME
                """);
        crs.next();
        assertEquals(2, crs.getInt(1));

        //language=SQL
        crs = dataAccess.getData("""
                SELECT "ID", "NAME"
                FROM RAEUME
                ORDER BY NAME
                """);
        crs.next();
        assertEquals(raum1.getId(), crs.getObject(1));
        assertEquals(raum1.getName(), crs.getString(2));
        crs.next();
        assertEquals(raum2.getId(), crs.getObject(1));
        assertEquals(raum2.getName(), crs.getString(2));
    }

    @Test
    void testUpdateRaumName() throws Exception {
        Raum raum1 = new Raum(UUID.fromString(UUID_1), RAUM_1);
        raumDataService.addRaum(raum1);
        raumDataService.updateRaumName(raum1.getId(), RAUM_2);

        //language=SQL
        CachedRowSet crs = dataAccess.getData("""
                SELECT "ID", "NAME"
                FROM RAEUME
                ORDER BY NAME
                """);
        crs.next();
        assertEquals(raum1.getId(), crs.getObject(1));
        assertEquals(RAUM_2, crs.getString(2));
    }

    @Test
    void testDeleteRaum() throws Exception {
        Raum raum1 = new Raum(UUID.fromString(UUID_1), RAUM_1);
        Raum raum2 = new Raum(UUID.fromString("0d481ee5-8528-42e0-bf14-e224e3d84ab0"), RAUM_2);
        raumDataService.addRaum(raum1);
        raumDataService.addRaum(raum2);

        raumDataService.deleteRaum(raum1.getId());
        //language=SQL
        CachedRowSet crs = dataAccess.getData("SELECT COUNT(*) FROM RAEUME");
        crs.next();
        assertEquals(1, crs.getInt(1));

        raumDataService.deleteRaum(raum2.getId());
        crs = dataAccess.getData("SELECT COUNT(*) FROM RAEUME");
        crs.next();
        assertEquals(0, crs.getInt(1));
    }

    @AfterEach
    void cleanUp() throws SQLException {
        //language=SQL
        dataAccess.executeUpdate("DELETE FROM RAEUME");
    }
}
