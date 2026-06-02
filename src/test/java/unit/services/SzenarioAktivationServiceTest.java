package unit.services;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.GeraetFactory;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import data.models.fachobjekte.geraeteArten.Sensor;
import data.services.datenServices.*;
import data.services.objektServices.GeraetObjektService;
import data.services.objektServices.RaumObjektService;
import data.services.objektServices.SzenarioAktivationService;
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

public class SzenarioAktivationServiceTest {
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
    private static Szenario.Aenderung aenderung2;
    private static SzenarioAktivationService szenarioAktivationService;


    @BeforeAll
    static void setUp() throws Exception {
        DataAccess.setTest(true);
        DatabaseCreationService.createDatabase();
        dataAccess = DataAccess.getInstance();
        dataAccess = DataAccess.getInstance();
        raumDataService = RaumDataService.getInstance();
        raumObjektService = RaumObjektService.getInstance();
        geraetDataService = GeraetDataService.getInstance();
        geraetObjektService = GeraetObjektService.getInstance();
        szenarioDataService = SzenarioDataService.getInstance();
        szenarioObjektService = SzenarioObjektService.getInstance();
        szenarioAktivationService = SzenarioAktivationService.getInstance();

        sensor1 = (Sensor) GeraetFactory.getInstance().createGeraet(SENSOR_1_ID, "Sensor 1", RAUM_1, "Sensor");
        sensor2 = (Sensor) GeraetFactory.getInstance().createGeraet(SENSOR_2_ID, "Sensor 2", RAUM_1, "Sensor");

        sensorWerte1 = new HashMap<>();
        sensorWerte1.put("eingeschaltet", "true");
        sensorWerte1.put("ausschlag", "true");
        sensorWerte2 = new HashMap<>();
        sensorWerte2.put("eingeschaltet", "false");
        sensorWerte2.put("ausschlag", "false");

        szenario1 = new Szenario(SZENARIO_1_ID, "Szenario 1");
        aenderung1 = szenarioObjektService.getAenderung(sensor1, "Sensor an", "eingeschaltet", "true");
        aenderung2 = szenarioObjektService.getAenderung(sensor2, "Sensor an", "eingeschaltet", "true");
    }

    @BeforeEach
    void setUpEach() throws SQLException {
        raumDataService.addRaum(RAUM_1);
        geraetDataService.addGeraet(sensor1, "Sensor", sensorWerte1);
        geraetDataService.addGeraet(sensor2, "Sensor", sensorWerte2);
        szenarioDataService.addSzenario(szenario1);
        szenarioDataService.addSzenarioInhalt(szenario1, aenderung1, 1);
        szenarioDataService.addSzenarioInhalt(szenario1, aenderung2, 2);

        Map<UUID, Raum> raumMap = raumObjektService.getAllRaeume();
        Map<UUID, Geraet> geraetMap = geraetObjektService.getAllGeraete(raumMap);
        szenarioObjektService.getAllSzenarien(geraetMap);
    }

    @Test
    void testAktiviereSzenario() {
        Szenario szenario = szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID);
        Sensor sensorLokal1 = (Sensor) szenario.getAenderungen().get(1).geraet();
        Sensor sensorLokal2 = (Sensor) szenario.getAenderungen().get(2).geraet();
        boolean erfolgreich = szenarioAktivationService.aktiviereSzenario(szenario);

        assertTrue(erfolgreich);
        assertFalse(StatusLog.hasError());
        assertTrue(szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID).isActive());
        assertEquals("true", sensorLokal1.getValues().get("eingeschaltet"));
        assertEquals("true", sensorLokal2.getValues().get("eingeschaltet"));
    }

    @Test
    void testDeaktiviereSzenario() {
        Szenario szenario = szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID);
        boolean erfolgreich = szenarioAktivationService.deaktiviereSzenario(szenario);

        assertTrue(erfolgreich);
        assertFalse(StatusLog.hasError());
        assertFalse(szenarioObjektService.getSzenarioMap().get(SZENARIO_1_ID).isActive());
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
