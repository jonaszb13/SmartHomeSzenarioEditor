package unit.services;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import data.services.datenServices.DataAccess;
import data.services.datenServices.DatabaseCreationService;
import data.services.datenServices.RaumDataService;
import data.services.objektServices.GeraetObjektService;
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

class GeraetObjektServiceTest {
    static final UUID RAUM_ID = UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4");
    static final UUID RAUM2_ID = UUID.fromString("04c18f4e-36db-4f65-816c-475577a044a2");
    static final Raum TEST_RAUM = new Raum(RAUM_ID, "Testraum");
    static final Raum TEST_RAUM_2 = new Raum(RAUM2_ID, "Testraum 2");

    static GeraetObjektService geraetObjektService;
    static DataAccess dataAccess;
    static RaumDataService raumDataService;

    @BeforeAll
    static void setUp() throws SQLException {
        DataAccess.setTest(true);
        DatabaseCreationService.createDatabase();
        dataAccess = DataAccess.getInstance();
        raumDataService = RaumDataService.getInstance();
        geraetObjektService = GeraetObjektService.getInstance();
    }

    @BeforeEach
    void init() throws Exception {
        StatusLog.clear();
        raumDataService.addRaum(TEST_RAUM);
        raumDataService.addRaum(TEST_RAUM_2);
        Map<UUID, Raum> raumMap = new HashMap<>();
        raumMap.put(RAUM_ID, TEST_RAUM);
        raumMap.put(RAUM2_ID, TEST_RAUM_2);
        geraetObjektService.getAllGeraete(raumMap);
    }

    @Test
    void testAddGeraet() {
        boolean result = geraetObjektService.addGeraet("Testlampe", "Lampe", TEST_RAUM, lampeAttr());

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals(1, geraetObjektService.getGeraetMap().size());
        Geraet geraet = geraetObjektService.getGeraetMap().values().iterator().next();
        assertEquals("Testlampe", geraet.getName());
        assertEquals(RAUM_ID, geraet.getRaum().getId());
    }

    @Test
    void testDeleteGeraet() {
        geraetObjektService.addGeraet("Testlampe", "Lampe", TEST_RAUM, lampeAttr());
        StatusLog.clear();
        UUID id = geraetObjektService.getGeraetMap().keySet().iterator().next();

        boolean result = geraetObjektService.deleteGeraet(id);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertFalse(geraetObjektService.getGeraetMap().containsKey(id));
    }

    @Test
    void testUpdateGeraetName() {
        geraetObjektService.addGeraet("Testlampe", "Lampe", TEST_RAUM, lampeAttr());
        StatusLog.clear();
        Geraet geraet = geraetObjektService.getGeraetMap().values().iterator().next();

        boolean result = geraetObjektService.updateGeraetName(geraet, "Neue Lampe");

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals("Neue Lampe", geraetObjektService.getGeraetMap().get(geraet.getId()).getName());
    }

    @Test
    void testUpdateGeraetRaum() {
        geraetObjektService.addGeraet("Testlampe", "Lampe", TEST_RAUM, lampeAttr());
        StatusLog.clear();
        Geraet geraet = geraetObjektService.getGeraetMap().values().iterator().next();

        boolean result = geraetObjektService.updateGeraetRaum(geraet, TEST_RAUM_2);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        assertEquals(RAUM2_ID, geraetObjektService.getGeraetMap().get(geraet.getId()).getRaum().getId());
    }

    @Test
    void testUpdateGeraetWerte() {
        geraetObjektService.addGeraet("Testlampe", "Lampe", TEST_RAUM, lampeAttr());
        StatusLog.clear();
        Geraet geraet = geraetObjektService.getGeraetMap().values().iterator().next();
        Map<String, String> neueWerte = new HashMap<>();
        neueWerte.put("eingeschaltet", "false");
        neueWerte.put("haelligkeit", "10.0");
        neueWerte.put("farbe", "#000000");

        boolean result = geraetObjektService.updateGeraetWerte(geraet, neueWerte);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
    }

    private static Map<String, String> lampeAttr() {
        Map<String, String> attr = new HashMap<>();
        attr.put("eingeschaltet", "true");
        attr.put("haelligkeit", "80.0");
        attr.put("farbe", "#FFFFFF");
        return attr;
    }

    @AfterEach
    void cleanUp() throws SQLException {
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM GERAETE_WERTE");
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM GERAETE");
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM RAEUME");
    }
}
