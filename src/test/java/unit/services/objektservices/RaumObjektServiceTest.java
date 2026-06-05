package unit.services.objektservices;

import data.models.fachobjekte.Raum;
import data.services.datenservices.DataAccess;
import data.services.datenservices.DatabaseCreationService;
import data.services.datenservices.RaumDataService;
import data.services.objektservices.RaumObjektService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.statusmeldungen.Meldung;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RaumObjektServiceTest {
    private static RaumObjektService raumObjektService;
    private static DataAccess dataAccess;

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

        //Testdaten
        RaumDataService raumDataService = RaumDataService.getInstance();
        UUID uuid = UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4");
        raumDataService.addRaum(new Raum(uuid, "Raum 1"));

        //Daten Laden
        raumObjektService.getAllRaeume();
    }

    @Test
    void testGetAllRaeume() throws SQLException {
        raumObjektService.getAllRaeume();
        Map<UUID, Raum> map = raumObjektService.getRaumMap();
        assertNotNull(map);
        assertEquals(1, map.size());
        assertFalse(StatusLog.hasError());
        assertEquals("RäumeMap erfolgreich geladen", StatusLog.getInstance().getStatusLogEintraege().getLast().getMeldungstext());
    }

    @Test
    void testAddRaum() {
        boolean result = raumObjektService.addRaum("Wohnzimmer");

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals(2, raumObjektService.getRaumMap().size());
        assertTrue(raumObjektService.getRaumMap().values().stream().anyMatch(o -> o.getName().equals("Wohnzimmer")));
        assertTrue(StatusLog.getInstance().getStatusLogEintraege().getLast().getMeldungstext().startsWith("Raum angelegt: Wohnzimmer ID: "));
    }

    @Test
    void testUpdateRaum() {
        StatusLog.clear();
        Raum raum = raumObjektService.getRaumMap().values().iterator().next();

        assertTrue(raumObjektService.updateRaum(raum, "Schlafzimmer"));
        assertFalse(StatusLog.hasError());
        assertEquals("Schlafzimmer", raumObjektService.getRaumMap().get(raum.getId()).getName());
        List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        assertEquals(1, meldungen.size());
        assertEquals("Name des Raums " + raum.getId() + " wurde auf Schlafzimmer geändert.", meldungen.getFirst().getMeldungstext());
    }

    @Test
    void testDeleteRaum() {
        StatusLog.clear();
        UUID id = raumObjektService.getRaumMap().values().stream().filter(o -> o.getName().equals("Raum 1")).findFirst().get().getId();

        boolean result = raumObjektService.deleteRaum(id);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertFalse(raumObjektService.getRaumMap().containsKey(id));
        assertEquals(0, raumObjektService.getRaumMap().size());
        List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        assertEquals(1, meldungen.size());
        assertEquals("Raum " + id + " wurde gelöscht.", meldungen.getFirst().getMeldungstext());
    }

    @AfterEach
    void cleanUp() throws SQLException {
        //language=SQL
        dataAccess.executeUpdate("DELETE FROM RAEUME");
    }
}
