package unit.models;

import data.models.Model;
import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Merkmalbezeichnung;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.geraetearten.Sensor;
import data.services.datenservices.DataAccess;
import data.services.datenservices.DatabaseCreationService;
import data.services.datenservices.GeraetDataService;
import data.services.datenservices.RaumDataService;
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

class ModelTest {

    static final UUID RAUM_1_ID = UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4");
    static final UUID RAUM_2_ID = UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb5");
    static final UUID SENSOR_ID = UUID.fromString("c216e129-1541-4455-804c-411b17dd015b");
    static final Raum RAUM_1 = new Raum(RAUM_1_ID, "Raum 1");
    static final Raum RAUM_2 = new Raum(RAUM_2_ID, "Raum 2");
    static final Sensor SENSOR = new Sensor(SENSOR_ID, "Testsensor", RAUM_1);

    private static Model model;
    private static DataAccess dataAccess;
    private static RaumDataService raumDataService;
    private static GeraetDataService geraetDataService;

    @BeforeAll
    static void setUp() throws SQLException {
        DataAccess.setTest(true);
        DatabaseCreationService.createDatabase();
        dataAccess = DataAccess.getInstance();
        raumDataService = RaumDataService.getInstance();
        geraetDataService = GeraetDataService.getInstance();
        StatusLog.clear();
        model = Model.getInstance();
    }

    @BeforeEach
    void init() throws SQLException {
        StatusLog.clear();
        raumDataService.addRaum(RAUM_1);
        raumDataService.addRaum(RAUM_2);
        Map<String, String> sensorWerte = new HashMap<>();
        sensorWerte.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        sensorWerte.put(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung(), "false");
        geraetDataService.addGeraet(SENSOR, "Sensor", sensorWerte);
        model.reload();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        dataAccess.executeUpdate("DELETE FROM GERAETE_WERTE");
        dataAccess.executeUpdate("DELETE FROM GERAETE");
        dataAccess.executeUpdate("DELETE FROM RAEUME");
    }

    @Test
    void getInstance_gibtNichtNullInstanzZurueck() {
        assertNotNull(model);
    }

    @Test
    void getInstance_gibtImmerGleicheInstanzZurueck() {
        assertSame(model, Model.getInstance());
    }

    @Test
    void konstruktor_statusbereichIstStatusLogInstanz() {
        assertSame(StatusLog.getInstance(), model.getStatusbereich());
    }

    @Test
    void konstruktor_raumMapIstInitialisiert() {
        assertNotNull(model.getRaumMap());
    }

    @Test
    void konstruktor_geraeteMapIstInitialisiert() {
        assertNotNull(model.getGeraete());
    }

    @Test
    void konstruktor_szenarioMapIstInitialisiert() {
        assertNotNull(model.getSzenarioMap());
    }

    @Test
    void konstruktor_keinFehlerBeimLaden() {
        assertFalse(StatusLog.hasError());
    }

    @Test
    void getAttributTypenFuerGeraetTyp_gueltigerTyp_gibtAttributeZurueck() {
        Map<String, Class<?>> typen = model.getAttributTypenFuerGeraetTyp("Sensor");

        assertFalse(typen.isEmpty());
        assertTrue(typen.containsKey(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung()));
        assertTrue(typen.containsKey(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung()));
    }

    @Test
    void getAttributTypenFuerGeraetTyp_ungueltigerTyp_gibtLeereMapZurueck() {
        Map<String, Class<?>> typen = model.getAttributTypenFuerGeraetTyp("NichtVorhandenerTyp");

        assertTrue(typen.isEmpty());
    }

    @Test
    void updateGeraetName_neuerName_gibtTrueZurueck() {
        Geraet geladenSensor = model.getGeraet(SENSOR_ID);

        boolean result = model.updateGeraetName(geladenSensor, "Neuer Name");

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals("Neuer Name", model.getGeraet(SENSOR_ID).getName());
    }

    @Test
    void updateGeraetName_gleicherName_gibtFalseZurueck() {
        Geraet geladenSensor = model.getGeraet(SENSOR_ID);

        boolean result = model.updateGeraetName(geladenSensor, geladenSensor.getName());

        assertFalse(result);
    }

    @Test
    void updateGeraetRaum_neuerRaum_gibtTrueZurueck() {
        Geraet geladenSensor = model.getGeraet(SENSOR_ID);

        boolean result = model.updateGeraetRaum(geladenSensor, RAUM_2);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals(RAUM_2_ID, model.getGeraet(SENSOR_ID).getRaum().getId());
    }

    @Test
    void updateGeraetRaum_gleicheRaum_gibtFalseZurueck() {
        Geraet geladenSensor = model.getGeraet(SENSOR_ID);
        Raum aktuellerRaum = geladenSensor.getRaum();

        boolean result = model.updateGeraetRaum(geladenSensor, aktuellerRaum);

        assertFalse(result);
    }
}
