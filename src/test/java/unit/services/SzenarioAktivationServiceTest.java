package unit.services;

import data.models.fachobjekte.*;
import data.models.fachobjekte.geraeteArten.Sensor;
import data.services.datenServices.*;
import data.services.objektServices.GeraetObjektService;
import data.services.objektServices.RaumObjektService;
import data.services.objektServices.SzenarioAusfuehrungsService;
import data.services.objektServices.SzenarioObjektService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private static Map<String, String> sensorWerte1;
    private static Map<String, String> sensorWerte2;
    private static Szenario szenario1;
    private static Szenario.Aenderung aenderung1;
    private static Szenario.Aenderung aenderung2;
    private static SzenarioAusfuehrungsService szenarioAktivationService;


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
        szenarioAktivationService = SzenarioAusfuehrungsService.getInstance();

        sensor1 = (Sensor) GeraetFactory.getInstance().createGeraet(SENSOR_1_ID, "Sensor 1", RAUM_1, "Sensor");
        sensor2 = (Sensor) GeraetFactory.getInstance().createGeraet(SENSOR_2_ID, "Sensor 2", RAUM_1, "Sensor");

        sensorWerte1 = new HashMap<>();
        sensorWerte1.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "true");
        sensorWerte1.put(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung(), "true");
        sensorWerte2 = new HashMap<>();
        sensorWerte2.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        sensorWerte2.put(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung(), "false");

        szenario1 = new Szenario(SZENARIO_1_ID, "Szenario 1");
        aenderung1 = szenarioObjektService.getAenderung(sensor1, "Sensor an", Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        aenderung2 = szenarioObjektService.getAenderung(sensor2, "Sensor an", Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "true");
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
        szenarioObjektService.ladeAlleSzenarien(geraetMap);
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
