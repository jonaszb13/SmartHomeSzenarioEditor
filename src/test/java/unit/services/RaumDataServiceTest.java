package unit.services;

import data.models.fachobjekte.Raum;
import data.services.datenServices.DataAccess;
import data.services.datenServices.DatabaseCreationService;
import data.services.datenServices.RaumDataService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RaumDataServiceTest {
    static DataAccess dataAccess;
    static RaumDataService raumDataService;
    public static final String ANZAHL_FEHLER = "Anzahl der Räume stimmt nicht";
    //language=SQL
    private static String raumMenge = """
            SELECT COUNT(*)
            FROM RAEUME
            """;

    @BeforeAll
    static void setup() {
        DataAccess.setTest(true);
        try {
            DatabaseCreationService.createDatabase();
            dataAccess = DataAccess.getInstance();
            raumDataService = RaumDataService.getInstance();
        } catch (SQLException e) {
            assert false;
        }
    }

    @BeforeEach
    void setUp() {
        try {
            //language=SQL
            dataAccess.executeTestUpdate("DELETE FROM RAEUME");
            DatabaseCreationService.createDatabase();
        } catch (SQLException e) {
            assert false;
        }
    }

    @Test
    void testGetRaum() {
        try {
            //language=SQL
            CachedRowSet crs = dataAccess.getTestRowSet(raumMenge);
            crs.next();
            int anzahlRaume = crs.getInt(1);
            assertEquals(0, anzahlRaume, ANZAHL_FEHLER);
        } catch (SQLException eSQL) {
            assert false;
        }
    }

    @Test
    void testAddRaum() {
        try {
            Raum raum1 = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
            Raum raum2 = new Raum(UUID.fromString("0d481ee5-8528-42e0-bf14-e224e3d84ab0"), "Raum 2");
            raumDataService.addRaum(raum1);
            raumDataService.addRaum(raum2);

            CachedRowSet crs = dataAccess.getTestRowSet(raumMenge);
            crs.next();
            int anzahlRaume = crs.getInt(1);
            assertEquals(2, anzahlRaume, ANZAHL_FEHLER);

            //language=SQL
            crs = dataAccess.getTestRowSet("""
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
        } catch (SQLException eSQL) {
            assert false;
        }
    }

    @Test
    void testUpdateRaumName() {
        try {
            Raum raum1 = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
            raumDataService.addRaum(raum1);
            raumDataService.updateRaumName(raum1.getId(), "Raum 2");

            CachedRowSet crs = dataAccess.getTestRowSet(raumMenge);
            crs.next();
            int anzahlRaume = crs.getInt(1);
            assertEquals(1, anzahlRaume, ANZAHL_FEHLER);

            //language=SQL
            crs = dataAccess.getTestRowSet("""
                    SELECT "ID", "NAME"
                    FROM RAEUME
                    ORDER BY NAME
                    """);
            crs.next();
            assertEquals(raum1.getId(), crs.getObject(1));
            assertEquals("Raum 2", crs.getString(2));
        } catch (SQLException eSQL) {
            assert false;
        }
    }

    @Test
    void testDeleteRaum() {
        try {
            Raum raum1 = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
            Raum raum2 = new Raum(UUID.fromString("0d481ee5-8528-42e0-bf14-e224e3d84ab0"), "Raum 2");
            raumDataService.addRaum(raum1);
            raumDataService.addRaum(raum2);

            CachedRowSet crs = dataAccess.getTestRowSet(raumMenge);
            crs.next();
            int anzahlRaume = crs.getInt(1);
            assertEquals(2, anzahlRaume, ANZAHL_FEHLER);

            raumDataService.deleteRaum(raum1.getId());
            crs = dataAccess.getTestRowSet(raumMenge);
            crs.next();
            anzahlRaume = crs.getInt(1);
            assertEquals(1, anzahlRaume, ANZAHL_FEHLER);

            raumDataService.deleteRaum(raum2.getId());
            crs = dataAccess.getTestRowSet(raumMenge);
            crs.next();
            anzahlRaume = crs.getInt(1);
            assertEquals(0, anzahlRaume, ANZAHL_FEHLER);
        } catch (SQLException eSQL) {
            assert false;
        }
    }
}
