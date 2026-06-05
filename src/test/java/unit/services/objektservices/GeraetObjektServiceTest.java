package unit.services.objektservices;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Merkmalbezeichnung;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.geraetearten.Lampe;
import data.models.fachobjekte.geraetearten.Sensor;
import data.services.datenservices.DataAccess;
import data.services.datenservices.DatabaseCreationService;
import data.services.datenservices.GeraetDataService;
import data.services.datenservices.RaumDataService;
import data.services.objektservices.GeraetObjektService;
import data.services.objektservices.RaumObjektService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.statusmeldungen.Meldung;
import util.statusmeldungen.StatusLog;

import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GeraetObjektServiceTest {
    static final UUID RAUM_1_ID = UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4");
    static final UUID RAUM_3_ID = UUID.fromString("04c18f4e-36db-4f65-816c-475577a044a2");
    static final Raum RAUM_1 = new Raum(RAUM_1_ID, "Raum 1");
    static final Raum RAUM_3 = new Raum(RAUM_3_ID, "Raum 2");
    static final UUID LAMPE_ID = UUID.fromString("fe4dacb0-7ef9-405f-be51-b739a4b6cd29");
    static final Lampe LAMPE_1 = new Lampe(LAMPE_ID, "Lampe 1", RAUM_3);
    static final UUID SENSOR_2_ID = UUID.fromString("ffe1118c-440c-40d4-bfc4-dadbfa5db831");
    static final Sensor SENSOR_2 = new Sensor(SENSOR_2_ID, "Sensor 2", RAUM_1);

    private static DataAccess dataAccess;
    private static RaumDataService raumDataService;
    private static RaumObjektService raumObjektService;
    private static GeraetDataService geraetDataService;
    private static GeraetObjektService geraetObjektService;


    @BeforeAll
    static void setUp() throws SQLException {
        DataAccess.setTest(true);
        DatabaseCreationService.createDatabase();
        dataAccess = DataAccess.getInstance();
        raumDataService = RaumDataService.getInstance();
        raumObjektService = RaumObjektService.getInstance();
        geraetDataService = GeraetDataService.getInstance();
        geraetObjektService = GeraetObjektService.getInstance();
    }

    private static Map<String, String> getLampenAttribute() {
        Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "true");
        attributeMap.put(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung(), "99.7");
        attributeMap.put(Merkmalbezeichnung.FARBE.getBezeichnung(), "#00FF88");
        return attributeMap;
    }

    @BeforeEach
    void init() throws Exception {
        StatusLog.clear();
        raumDataService.addRaum(RAUM_1);
        raumDataService.addRaum(RAUM_3);
        geraetDataService.addGeraet(LAMPE_1, "Lampe", getLampenAttribute());
        Map<String, String> sensorWerte = new HashMap<>();
        sensorWerte.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        sensorWerte.put(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung(), "false");
        geraetDataService.addGeraet(SENSOR_2, "Sensor", sensorWerte);

        raumObjektService.getAllRaeume();
        Map<UUID, Raum> raumMap = raumObjektService.getRaumMap();
        geraetObjektService.getAllGeraete(raumMap);
    }

    @Test
    void testDeleteGeraet() {
        StatusLog.clear();

        boolean erfolgreich = geraetObjektService.deleteGeraet(LAMPE_ID);

        assertTrue(erfolgreich);
        assertFalse(StatusLog.hasError());
        assertEquals(1, geraetObjektService.getGeraetMap().size());
        assertFalse(geraetObjektService.getGeraetMap().containsKey(LAMPE_ID));
        List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        assertEquals(1, meldungen.size());
        assertEquals("Gerät mit id " + LAMPE_ID + " erfolgreich gelöscht", meldungen.getFirst().getMeldungstext());
    }

    @Test
    void testUpdateGeraetName() {
        StatusLog.clear();

        boolean erfolgreich = geraetObjektService.updateGeraetName(LAMPE_1, "Neue Lampe");

        assertTrue(erfolgreich);
        assertFalse(StatusLog.hasError());
        assertEquals("Neue Lampe", geraetObjektService.getGeraetMap().get(LAMPE_ID).getName());
        List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        assertEquals(1, meldungen.size());
        assertEquals("Name des Geräts " + LAMPE_ID + " erfolgreich zu Neue Lampe geändert", meldungen.getFirst().getMeldungstext());
    }

    @Test
    void testUpdateGeraetRaum() {
        StatusLog.clear();

        boolean result = geraetObjektService.updateGeraetRaum(LAMPE_1, RAUM_1);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals(RAUM_1, geraetObjektService.getGeraetMap().get(LAMPE_ID).getRaum());
        List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        assertEquals(1, meldungen.size());
        assertEquals("Raum des Geräts " + LAMPE_1.getName() + " erfolgreich zu " + RAUM_1.getName() + " geändert", meldungen.getFirst().getMeldungstext());
    }

    @Test
    void testAddGeraet() {
        StatusLog.clear();
        Map<String, String> attributemap = new HashMap<>();
        attributemap.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "true");
        attributemap.put(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung(), "true");

        boolean erfolgreich = geraetObjektService.addGeraet("Sensor 1", "Sensor", RAUM_1, attributemap);

        assertTrue(erfolgreich);
        assertFalse(StatusLog.hasError());
        assertEquals(3, geraetObjektService.getGeraetMap().size());
        Geraet geraet = geraetObjektService.getGeraetMap().values().stream().filter(o -> o.getName().equals("Sensor 1")).findFirst().get();
        assertEquals(RAUM_1, geraet.getRaum());
        List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        assertEquals(1, meldungen.size());
        assertTrue(meldungen.getFirst().getMeldungstext().startsWith("Neues Gerät hinzugefügt. ID: "));
    }

    @Test
    void testUpdateGeraetWerte() {
        StatusLog.clear();
        Map<String, String> neueWerte = new HashMap<>();
        neueWerte.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        neueWerte.put(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung(), "10,0");
        neueWerte.put(Merkmalbezeichnung.FARBE.getBezeichnung(), "#000000");

        boolean result = geraetObjektService.updateGeraetWerte(LAMPE_1, neueWerte);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertFalse(LAMPE_1.isEingeschaltet());
        assertEquals(10, LAMPE_1.getHelligkeit());
        assertEquals(Color.decode("#000000"), LAMPE_1.getFarbe());
        List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        assertFalse(meldungen.isEmpty());
        assertTrue(meldungen.stream().noneMatch(Meldung::isError));
        assertTrue(meldungen.stream().anyMatch(m ->
                m.getMeldungstext().contains(LAMPE_1.getName()) &&
                m.getMeldungstext().contains(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung())
        ));
    }

    @AfterEach
    void cleanUp() throws SQLException {
        //language=SQL
        dataAccess.executeUpdate("DELETE FROM GERAETE_WERTE");
        //language=SQL
        dataAccess.executeUpdate("DELETE FROM GERAETE");
        //language=SQL
        dataAccess.executeUpdate("DELETE FROM RAEUME");
    }
}
