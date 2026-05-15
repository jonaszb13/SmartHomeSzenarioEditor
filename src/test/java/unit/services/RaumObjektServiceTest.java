package unit.services;

import data.models.fachobjekte.Raum;
import data.services.datenServices.DataAccess;
import data.services.datenServices.DatabaseCreationService;
import data.services.objektServices.RaumObjektService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RaumObjektServiceTest {
    static RaumObjektService raumObjektService;
    static DataAccess dataAccess;

    @BeforeAll
    static void setUp() throws SQLException {
        DataAccess.setTest(true);
        DatabaseCreationService.createDatabase();
        dataAccess = DataAccess.getInstance();
        raumObjektService = RaumObjektService.getInstance();
    }

    @BeforeEach
    void initRaumMap() throws SQLException {
        StatusLog.clear();
        raumObjektService.getAllRaeume();
    }

    @Test
    void testAddRaum() {
        boolean result = raumObjektService.addRaum("Wohnzimmer");

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals(1, raumObjektService.getRaumMap().size());
        Raum raum = raumObjektService.getRaumMap().values().iterator().next();
        assertEquals("Wohnzimmer", raum.getName());
    }

    @Test
    void testUpdateRaum() {
        raumObjektService.addRaum("Küche");
        StatusLog.clear();
        Raum raum = raumObjektService.getRaumMap().values().iterator().next();

        boolean result = raumObjektService.updateRaum(raum, "Schlafzimmer");

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals("Schlafzimmer", raumObjektService.getRaumMap().get(raum.getId()).getName());
    }

    @Test
    void testDeleteRaum() {
        raumObjektService.addRaum("Bad");
        StatusLog.clear();
        UUID id = raumObjektService.getRaumMap().keySet().iterator().next();

        boolean result = raumObjektService.deleteRaum(id);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertFalse(raumObjektService.getRaumMap().containsKey(id));
        assertEquals(0, raumObjektService.getRaumMap().size());
    }

    @AfterEach
    void cleanUp() throws SQLException {
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM RAEUME");
    }
}
