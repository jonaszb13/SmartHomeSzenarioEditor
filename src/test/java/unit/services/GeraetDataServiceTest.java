package unit.services;

import data.models.fachobjekte.GeraetFactory;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.geraeteArten.Lampe;
import data.models.fachobjekte.geraeteArten.Sensor;
import data.services.datenServices.DataAccess;
import data.services.datenServices.DatabaseCreationService;
import data.services.datenServices.GeraetDataService;
import data.services.datenServices.RaumDataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeraetDataServiceTest {
    private static final String LAMPE = "Lampe";
    private static final String SENSOR = "Sensor";
    private static final String HAELLIGKEIT = "haelligkeit";
    private static final String FARBE = "farbe";
    private static final String EINGESCHALTET = "eingeschaltet";
    private static final String TRUE = "true";
    private static DataAccess dataAccess;
    private static RaumDataService raumDataService;
    private static GeraetDataService geraetDataService;
    private static GeraetFactory geraetFactory;
    //language=SQL
    private static final String GERAET_MENGE = "SELECT COUNT(*) FROM GERAETE";

    @BeforeAll
    static void setup() throws SQLException {
        DataAccess.setTest(true);
        DatabaseCreationService.createDatabase();
        dataAccess = DataAccess.getInstance();
        raumDataService = RaumDataService.getInstance();
        geraetDataService = GeraetDataService.getInstance();
        geraetFactory = GeraetFactory.getInstance();
    }

    @Test
    void testAddGeraet() throws Exception {
        Raum raum = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
        Raum raum2 = new Raum(UUID.fromString("04c18f4e-36db-4f65-816c-475577a044a2"), "Raum 3");
        Lampe lampe = (Lampe) geraetFactory.createGeraet(UUID.fromString("fe4dacb0-7ef9-405f-be51-b739a4b6cd29"),
                "Lampe 1", raum2, LAMPE);
        Sensor sensor = (Sensor) geraetFactory.createGeraet(UUID.fromString("c216e129-1541-4455-804c-411b17dd015b"),
                "Sensor 1", raum, SENSOR);
        Map<String, String> lampeMap = new HashMap<>();
        Map<String, String> sensorMap = new HashMap<>();
        lampeMap.put(HAELLIGKEIT, "99.7");
        lampeMap.put(FARBE, "#00FF88");
        lampeMap.put(EINGESCHALTET, TRUE);
        sensorMap.put(EINGESCHALTET, TRUE);
        sensorMap.put("ausschlag", TRUE);

        //Überprüfen, dass leer
        CachedRowSet crs = dataAccess.getData(GERAET_MENGE);
        crs.next();
        assertEquals(0, crs.getInt(1));

        //einfügen
        raumDataService.addRaum(raum);
        raumDataService.addRaum(raum2);
        geraetDataService.addGeraet(sensor, SENSOR, sensorMap);
        geraetDataService.addGeraet(lampe, LAMPE, lampeMap);

        //Prüfen, dass 2 Geräte in der Datenbank sind
        crs = dataAccess.getData(GERAET_MENGE);
        crs.next();
        assertEquals(2, crs.getInt(1));

        //language=SQL
        crs = dataAccess.getData("""
                SELECT GERAETE.ID, NAME, RAUM, ART, SCHLUESSEL, WERT
                FROM GERAETE
                JOIN GERAETE_WERTE ON GERAETE.ID = GERAETE_WERTE.GERAET
                ORDER BY GERAETE.ID
                """);
        while (crs.next()) {
            UUID currentId = (UUID) crs.getObject(1);
            if (currentId.equals(sensor.getId())) {
                assertEquals(sensor.getName(), crs.getString(2));
                assertEquals(sensor.getRaum().getId(), crs.getObject(3));
                assertEquals(SENSOR, crs.getString(4));
                assertEquals(sensorMap.get(crs.getString(5)), crs.getString(6));
            } else {
                assertEquals(lampe.getName(), crs.getString(2));
                assertEquals(lampe.getRaum().getId(), crs.getObject(3));
                assertEquals(LAMPE, crs.getString(4));
                assertEquals(lampeMap.get(crs.getString(5)), crs.getString(6));
            }
        }
    }

    @Test
    void testDeleteGeraet() throws Exception {
        Raum raum = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
        Raum raum2 = new Raum(UUID.fromString("04c18f4e-36db-4f65-816c-475577a044a2"), "Raum 3");
        Lampe lampe = (Lampe) geraetFactory.createGeraet(UUID.fromString("fe4dacb0-7ef9-405f-be51-b739a4b6cd29"),
                "Lampe 1", raum2, LAMPE);
        Sensor sensor = (Sensor) geraetFactory.createGeraet(UUID.fromString("c216e129-1541-4455-804c-411b17dd015b"),
                "Sensor 1", raum, SENSOR);
        Map<String, String> lampeMap = new HashMap<>();
        Map<String, String> sensorMap = new HashMap<>();
        lampeMap.put(HAELLIGKEIT, "99.7");
        lampeMap.put(FARBE, "#00FF88");
        lampeMap.put(EINGESCHALTET, TRUE);
        sensorMap.put(EINGESCHALTET, TRUE);
        sensorMap.put("ausschlag", TRUE);
        
        //einfügen
        raumDataService.addRaum(raum);
        raumDataService.addRaum(raum2);
        geraetDataService.addGeraet(sensor, SENSOR, sensorMap);
        geraetDataService.addGeraet(lampe, LAMPE, lampeMap);

        //Prüfen, dass 2 Geräte in der Datenbank sind
        CachedRowSet crs = dataAccess.getData(GERAET_MENGE);
        crs.next();
        assertEquals(2, crs.getInt(1));

        //Prüfen, dass 1 Gerät in Datenbank ist
        geraetDataService.deleteGeraet(lampe);
        crs = dataAccess.getData(GERAET_MENGE);
        crs.next();
        assertEquals(1, crs.getInt(1));

        //Überprüfen, dass leer 
        geraetDataService.deleteGeraet(sensor);
        crs = dataAccess.getData(GERAET_MENGE);
        crs.next();
        assertEquals(0, crs.getInt(1));
    }

    @Test
    void testUpdateGeraet() throws Exception {
        Raum raum = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
        Raum raum2 = new Raum(UUID.fromString("04c18f4e-36db-4f65-816c-475577a044a2"), "Raum 3");
        Lampe lampe = (Lampe) geraetFactory.createGeraet(UUID.fromString("fe4dacb0-7ef9-405f-be51-b739a4b6cd29"),
                "Lampe 1", raum2, LAMPE);
        Map<String, String> lampeMap = new HashMap<>();
        lampeMap.put(HAELLIGKEIT, "99.7");
        lampeMap.put(FARBE, "#00FF88");
        lampeMap.put(EINGESCHALTET, TRUE);
        Map<String, String> lampeMap2 = new HashMap<>();
        lampeMap2.put(HAELLIGKEIT, "50.12");
        lampeMap2.put(FARBE, "#1234AB");
        lampeMap2.put(EINGESCHALTET, "false");

        //einfügen
        raumDataService.addRaum(raum);
        raumDataService.addRaum(raum2);
        geraetDataService.addGeraet(lampe, LAMPE, lampeMap);

        //Prüfen, dass 1 Gerät in der Datenbank ist
        //language=SQL
        String sql = """
                SELECT GERAETE.ID, NAME, RAUM, ART, SCHLUESSEL, WERT
                FROM GERAETE
                JOIN GERAETE_WERTE ON GERAETE.ID = GERAETE_WERTE.GERAET
                ORDER BY GERAETE.ID
                """;
        CachedRowSet crs = dataAccess.getData(sql);
        while (crs.next()) {
            assertEquals(lampe.getId(), crs.getObject(1));
            assertEquals(lampe.getName(), crs.getString(2));
            assertEquals(raum2.getId(), crs.getObject(3));
            assertEquals(LAMPE, crs.getString(4));
            assertEquals(lampeMap.get(crs.getString(5)), crs.getString(6));
        }

        geraetDataService.updateGeraetName(lampe, "Lampe 2");
        geraetDataService.updateGeraetRaum(lampe, raum);
        geraetDataService.updateGeraetWerte(lampe, lampeMap2);

        crs = dataAccess.getData(sql);
        while (crs.next()) {
            assertEquals(lampe.getId(), crs.getObject(1));
            assertEquals("Lampe 2", crs.getString(2));
            assertEquals(raum.getId(), crs.getObject(3));
            assertEquals(LAMPE, crs.getString(4));
            assertEquals(lampeMap2.get(crs.getString(5)), crs.getString(6));
        }
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