package unit.services;

import data.models.fachobjekte.GeraetFactory;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import data.models.fachobjekte.geraeteArten.Sensor;
import data.services.datenServices.*;
import data.services.objektServices.SzenarioObjektService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SzenarioObjektServiceTest {
    static final UUID RAUM_ID = UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4");
    static final UUID SENSOR_ID = UUID.fromString("c216e129-1541-4455-804c-411b17dd015b");
    static final Raum TEST_RAUM = new Raum(RAUM_ID, "Testraum");

    private static SzenarioObjektService szenarioObjektService;
    private static DataAccess dataAccess;
    private static RaumDataService raumDataService;
    private static GeraetDataService geraetDataService;
    private static Sensor testSensor;

    @BeforeAll
    static void setUp() throws Exception {
        DataAccess.setTest(true);
        DatabaseCreationService.createDatabase();
        dataAccess = DataAccess.getInstance();
        raumDataService = RaumDataService.getInstance();
        geraetDataService = GeraetDataService.getInstance();
        szenarioObjektService = SzenarioObjektService.getInstance();
        testSensor = (Sensor) GeraetFactory.getInstance().createGeraet(SENSOR_ID, "Testsensor", TEST_RAUM, "Sensor");
    }

    @BeforeEach
    void init() throws Exception {
        StatusLog.clear();
        raumDataService.addRaum(TEST_RAUM);
        geraetDataService.addGeraet(testSensor, "Sensor", new HashMap<>());
        Map<UUID, data.models.fachobjekte.Geraet> geraetMap = new HashMap<>();
        geraetMap.put(SENSOR_ID, testSensor);
        szenarioObjektService.getAllSzenarien(geraetMap);
    }

    @Test
    void testAddSzenario() {
        Map<Integer, Szenario.Aenderung> aenderungen = new HashMap<>();
        aenderungen.put(1, new Szenario.Aenderung(UUID.randomUUID(), testSensor, "Sensor an", "eingeschaltet", "true"));

        boolean result = szenarioObjektService.addSzenario("Testszenario", "Beschreibung", aenderungen);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals(1, szenarioObjektService.getSzenarioMap().size());
        Szenario szenario = szenarioObjektService.getSzenarioMap().values().iterator().next();
        assertEquals("Testszenario", szenario.getName());
        assertEquals("Beschreibung", szenario.getBeschreibung());
        assertEquals(1, szenario.getAenderungen().size());
    }

    @Test
    void testUpdateSzenario() {
        szenarioObjektService.addSzenario("Alt", "Alte Beschreibung", new HashMap<>());
        StatusLog.clear();
        Szenario szenario = szenarioObjektService.getSzenarioMap().values().iterator().next();

        boolean result = szenarioObjektService.updateSzenario(szenario, "Neu", "Neue Beschreibung");

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals("Neu", szenarioObjektService.getSzenarioMap().get(szenario.getId()).getName());
        assertEquals("Neue Beschreibung", szenarioObjektService.getSzenarioMap().get(szenario.getId()).getBeschreibung());
    }

    @Test
    void testDeleteSzenario() {
        szenarioObjektService.addSzenario("Zu löschen", "Beschreibung", new HashMap<>());
        StatusLog.clear();
        Szenario szenario = szenarioObjektService.getSzenarioMap().values().iterator().next();

        boolean result = szenarioObjektService.deleteSzenario(szenario);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertFalse(szenarioObjektService.getSzenarioMap().containsKey(szenario.getId()));
    }

    @Test
    void testAddSzenarioInhalt() {
        szenarioObjektService.addSzenario("Szenario", "Beschreibung", new HashMap<>());
        StatusLog.clear();
        Szenario szenario = szenarioObjektService.getSzenarioMap().values().iterator().next();
        Szenario.Aenderung aenderung = new Szenario.Aenderung(UUID.randomUUID(), testSensor, "Sensor an", "eingeschaltet", "true");

        boolean result = szenarioObjektService.addSzenarioInhalt(szenario, aenderung, 1);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals(1, szenario.getAenderungen().size());
        assertEquals(aenderung.id(), szenario.getAenderungen().get(1).id());
    }

    @Test
    void testUpdateSzenarioInhalt() {
        Szenario.Aenderung aenderung = new Szenario.Aenderung(UUID.randomUUID(), testSensor, "Sensor an", "eingeschaltet", "true");
        Map<Integer, Szenario.Aenderung> aenderungen = new HashMap<>();
        aenderungen.put(1, aenderung);
        szenarioObjektService.addSzenario("Szenario", "Beschreibung", aenderungen);
        StatusLog.clear();
        Szenario szenario = szenarioObjektService.getSzenarioMap().values().iterator().next();

        boolean result = szenarioObjektService.alterSzenarioInhalt(szenario, 1, testSensor, "Sensor aus", "eingeschaltet", "false");

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals("false", szenario.getAenderungen().get(1).wert());
        assertEquals("Sensor aus", szenario.getAenderungen().get(1).beschreibung());
    }

    @Test
    void testDeleteSzenarioInhalt() {
        Szenario.Aenderung aenderung = new Szenario.Aenderung(UUID.randomUUID(), testSensor, "Sensor an", "eingeschaltet", "true");
        Map<Integer, Szenario.Aenderung> aenderungen = new HashMap<>();
        aenderungen.put(1, aenderung);
        szenarioObjektService.addSzenario("Szenario", "Beschreibung", aenderungen);
        StatusLog.clear();
        Szenario szenario = szenarioObjektService.getSzenarioMap().values().iterator().next();

        boolean result = szenarioObjektService.deleteSzenarioInhalt(szenario, 1);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertFalse(szenario.getAenderungen().containsKey(1));
    }

    @AfterEach
    void cleanUp() throws SQLException {
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM SZENARIEN_INHALT");
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM SZENARIEN");
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM GERAETE_WERTE");
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM GERAETE");
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM RAEUME");
    }
}
