package unit.services.objektservices;

import data.models.fachobjekte.*;
import data.models.fachobjekte.geraeteArten.Sensor;
import data.services.datenServices.*;
import data.services.objektServices.GeraetObjektService;
import data.services.objektServices.RaumObjektService;
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
    static final UUID SENSOR_1_ID = UUID.fromString("c216e129-1541-4455-804c-411b17dd015b");
    static final UUID SENSOR_2_ID = UUID.fromString("ffe1118c-440c-40d4-bfc4-dadbfa5db831");
    static final UUID SZENARIO_1_ID = UUID.fromString("c06ecee9-65c3-4444-aa82-e1148badfc0d");
    static final Raum RAUM_1 = new Raum(RAUM_ID, "Raum 1");


    private static DataAccess dataAccess;
    private static RaumDataService raumDataService;
    private static RaumObjektService raumObjektService;
    private static GeraetDataService geraetDataService;
    private static GeraetObjektService geraetObjektService;
    private static SzenarioDataService szenarioDataService;
    private static SzenarioObjektService szenarioObjektService;
    private static Sensor sensor1;
    private static Sensor sensor2;
    private static HashMap<String, String> sensorWerte1;
    private static HashMap<String, String> sensorWerte2;
    private static Szenario szenario1;
    private static Szenario.Aenderung aenderung1;


    @BeforeAll
    static void setUp() throws Exception {
        DataAccess.setTest(true);
        DatabaseCreationService.createDatabase();
        dataAccess = DataAccess.getInstance();
        raumDataService = RaumDataService.getInstance();
        raumObjektService = RaumObjektService.getInstance();
        geraetDataService = GeraetDataService.getInstance();
        geraetObjektService = GeraetObjektService.getInstance();
        szenarioDataService = SzenarioDataService.getInstance();
        szenarioObjektService = SzenarioObjektService.getInstance();

        sensor1 = (Sensor) GeraetFactory.getInstance().createGeraet(SENSOR_1_ID, "Sensor 1", RAUM_1, "Sensor");
        sensor2 = (Sensor) GeraetFactory.getInstance().createGeraet(SENSOR_2_ID, "Sensor 2", RAUM_1, "Sensor");

        sensorWerte1 = new HashMap<>();
        sensorWerte1.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "true");
        sensorWerte1.put(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung(), "true");
        sensorWerte2 = new HashMap<>();
        sensorWerte2.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        sensorWerte2.put(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung(), "false");

        szenario1 = new Szenario(SZENARIO_1_ID, "Szenario 1");
        aenderung1 = szenarioObjektService.getAenderung(sensor2, "Sensor an", Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "true");

    }

    @BeforeEach
    void init() throws Exception {
        StatusLog.clear();

        raumDataService.addRaum(RAUM_1);
        geraetDataService.addGeraet(sensor1, "Sensor", sensorWerte1);
        geraetDataService.addGeraet(sensor2, "Sensor", sensorWerte2);
        szenarioDataService.addSzenario(szenario1);
        szenarioDataService.addSzenarioInhalt(szenario1, aenderung1, 2);

        Map<UUID, Raum> raumMap = raumObjektService.getAllRaeume();
        Map<UUID, Geraet> geraetMap = geraetObjektService.getAllGeraete(raumMap);
        szenarioObjektService.ladeAlleSzenarien(geraetMap);

    }

    @Test
    void testAddSzenario() {
        Map<Integer, Szenario.Aenderung> aenderungen = new HashMap<>();
        aenderungen.put(1, szenarioObjektService.getAenderung(sensor1, "Sensor aus", Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false"));

        boolean result = szenarioObjektService.addSzenario("Szenario 2", "Sensor aus", aenderungen);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals(2, szenarioObjektService.getSzenarioMap().size());
        Szenario szenario = szenarioObjektService.getSzenarioMap().values().stream().filter(o -> o.getName().equals("Szenario 2")).findFirst().get();
        assertEquals("Sensor aus", szenario.getBeschreibung());
        assertEquals(1, szenario.getAenderungen().size());
        assertEquals(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), szenario.getAenderungen().get(1).schluessel());
        assertEquals("false", szenario.getAenderungen().get(1).wert());
        assertEquals("Sensor aus", szenario.getAenderungen().get(1).beschreibung());
    }

    @Test
    void testUpdateSzenarioName() {
        StatusLog.clear();
        Szenario szenario = szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID);
        boolean result = szenarioObjektService.updateSzenarioName(szenario, "Neu");

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals("Neu", szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID).getName());

    }

    @Test
    void testupdateSzenarioBeschreibung() {
        StatusLog.clear();
        Szenario szenario = szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID);
        boolean result = szenarioObjektService.updateSzenarioBeschreibung(szenario, "Neue Beschreibung");

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals("Neue Beschreibung", szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID).getBeschreibung());
    }

    @Test
    void testUpdateSzenario() {
        StatusLog.clear();
        Szenario szenario = szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID);
        boolean result = szenarioObjektService.updateSzenario(szenario, "Neu", "Neue Beschreibung");

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals("Neu", szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID).getName());
        assertEquals("Neue Beschreibung", szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID).getBeschreibung());
    }

    @Test
    void testDeleteSzenario() {
        StatusLog.clear();
        Szenario szenario = szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID);

        boolean result = szenarioObjektService.deleteSzenario(szenario);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertFalse(szenarioObjektService.getSzenarioMap().containsKey(szenario.getId()));
    }

    @Test
    void testAddSzenarioInhalt() {
        StatusLog.clear();
        Szenario szenario = szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID);
        Szenario.Aenderung aenderung = szenarioObjektService.getAenderung(sensor1, "Sensor an", Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "true");

        boolean result = szenarioObjektService.addSzenarioInhalt(szenario, aenderung, 1);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals(2, szenario.getAenderungen().size());
        assertEquals(aenderung, szenario.getAenderungen().get(1));
    }

    @Test
    void testUpdateSzenarioInhalt() {
        StatusLog.clear();
        Szenario szenario = szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID);

        boolean result = szenarioObjektService.alterSzenarioInhalt(szenario, 2, sensor1, "Sensor aus", Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals(sensor1, szenario.getAenderungen().get(2).geraet());
        assertEquals(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), szenario.getAenderungen().get(2).schluessel());
        assertEquals("false", szenario.getAenderungen().get(2).wert());
        assertEquals("Sensor aus", szenario.getAenderungen().get(2).beschreibung());
    }

    @Test
    void testDeleteSzenarioInhalt() {
        StatusLog.clear();
        Szenario szenario = szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID);

        boolean result = szenarioObjektService.deleteSzenarioInhalt(szenario, 2);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertFalse(szenario.getAenderungen().containsKey(1));
    }

    @AfterEach
    void cleanUp() throws SQLException {
        //language=SQL
        dataAccess.executeUpdate("DELETE FROM SZENARIEN_INHALT");
        //language=SQL
        dataAccess.executeUpdate("DELETE FROM SZENARIEN");
        //language=SQL
        dataAccess.executeUpdate("DELETE FROM GERAETE_WERTE");
        //language=SQL
        dataAccess.executeUpdate("DELETE FROM GERAETE");
        //language=SQL
        dataAccess.executeUpdate("DELETE FROM RAEUME");
    }
}
