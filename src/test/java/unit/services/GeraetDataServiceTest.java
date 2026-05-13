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

public class GeraetDataServiceTest {
    static DataAccess dataAccess;
    static RaumDataService raumDataService;
    static GeraetDataService geraetDataService;
    static GeraetFactory geraetFactory;
    //language=SQL
    static final String geraetMenge = """
            SELECT COUNT(*)
            FROM GERAETE
            """;

    @BeforeAll
    static void setup() {
        DataAccess.setTest(true);
        try {
            DatabaseCreationService.createDatabase();
            dataAccess = DataAccess.getInstance();
            raumDataService = RaumDataService.getInstance();
            geraetDataService = GeraetDataService.getInstance();
            geraetFactory = GeraetFactory.getInstance();
        } catch (SQLException e) {
            assert false;
        }
    }

    @Test
    void testAddGeraet() {
        try {
            //Testobjekte Erzeugen
            Raum raum = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
            Raum raum2 = new Raum(UUID.fromString("04c18f4e-36db-4f65-816c-475577a044a2"), "Raum 3");
            Lampe lampe = (Lampe) geraetFactory.createGeraet(UUID.fromString("fe4dacb0-7ef9-405f-be51-b739a4b6cd29"),
                    "Lampe 1", raum2, "Lampe");
            Sensor sensor = (Sensor) geraetFactory.createGeraet(UUID.fromString("c216e129-1541-4455-804c-411b17dd015b"),
                    "Sensor 1", raum, "Sensor");
            Map<String, String> lampeMap = new HashMap<>();
            Map<String, String> sensorMap = new HashMap<>();
            lampeMap.put("haelligkeit", "99.7");
            lampeMap.put("farbe", "#00FF88");
            lampeMap.put("eingeschaltet", "true");
            sensorMap.put("eingeschaltet", "true");
            sensorMap.put("ausschlag", "true");

            //Überprüfen, dass leer
            CachedRowSet crs = dataAccess.getData(geraetMenge);
            crs.next();
            int anzahlGeraete = crs.getInt(1);
            assertEquals(0, anzahlGeraete);

            //einfügen
            raumDataService.addRaum(raum);
            raumDataService.addRaum(raum2);
            geraetDataService.addGeraet(sensor, "Sensor", sensorMap);
            geraetDataService.addGeraet(lampe, "Lampe", lampeMap);

            //Prüfen, dass 2 Geräte in Datenbank sind
            crs = dataAccess.getData(geraetMenge);
            crs.next();
            anzahlGeraete = crs.getInt(1);
            assertEquals(2, anzahlGeraete);

            //language=SQL
            crs = dataAccess.getData("""
                    SELECT GERAETE.ID, NAME, RAUM, ART, SCHLUESSEL, WERT
                    FROM GERAETE
                    JOIN GERAETE_WERTE
                    ON GERAETE.ID = GERAETE_WERTE.GERAET
                    ORDER BY GERAETE.ID
                    """);
            for (int i = 0; i < 5; i++) {
                crs.next();
                if (i < 2) {
                    assertEquals(crs.getObject(1), sensor.getId());
                    assertEquals(crs.getString(2), sensor.getName());
                    assertEquals(crs.getObject(3), sensor.getRaum().getId());
                    assertEquals("Sensor", crs.getObject(4));
                    assertEquals(crs.getString(6), sensorMap.get(crs.getString(5)));
                } else {
                    assertEquals(crs.getObject(1), lampe.getId());
                    assertEquals(crs.getString(2), lampe.getName());
                    assertEquals(crs.getObject(3), lampe.getRaum().getId());
                    assertEquals("Lampe", crs.getObject(4));
                    assertEquals(crs.getString(6), lampeMap.get(crs.getString(5)));
                }
            }
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testDeleteGeraet() {
        try {
            //Testobjekte Erzeugen
            Raum raum = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
            Raum raum2 = new Raum(UUID.fromString("04c18f4e-36db-4f65-816c-475577a044a2"), "Raum 3");
            Lampe lampe = (Lampe) geraetFactory.createGeraet(UUID.fromString("fe4dacb0-7ef9-405f-be51-b739a4b6cd29"),
                    "Lampe 1", raum2, "Lampe");
            Sensor sensor = (Sensor) geraetFactory.createGeraet(UUID.fromString("c216e129-1541-4455-804c-411b17dd015b"),
                    "Sensor 1", raum, "Sensor");
            Map<String, String> lampeMap = new HashMap<>();
            Map<String, String> sensorMap = new HashMap<>();
            lampeMap.put("haelligkeit", "99.7");
            lampeMap.put("farbe", "#00FF88");
            lampeMap.put("eingeschaltet", "true");
            sensorMap.put("eingeschaltet", "true");
            sensorMap.put("ausschlag", "true");

            //Überprüfen, dass leer
            CachedRowSet crs = dataAccess.getData(geraetMenge);
            crs.next();
            int anzahlGeraete = crs.getInt(1);
            assertEquals(0, anzahlGeraete);

            //einfügen
            raumDataService.addRaum(raum);
            raumDataService.addRaum(raum2);
            geraetDataService.addGeraet(sensor, "Sensor", sensorMap);
            geraetDataService.addGeraet(lampe, "Lampe", lampeMap);

            //Prüfen, dass 2 Geräte in Datenbank sind
            crs = dataAccess.getData(geraetMenge);
            crs.next();
            anzahlGeraete = crs.getInt(1);
            assertEquals(2, anzahlGeraete);

            geraetDataService.deleteGeraet(lampe);

            //Prüfen, dass 1 Geräte in Datenbank ist
            crs = dataAccess.getData(geraetMenge);
            crs.next();
            anzahlGeraete = crs.getInt(1);
            assertEquals(1, anzahlGeraete);

            geraetDataService.deleteGeraet(sensor);

            //Überprüfen, dass leer
            crs = dataAccess.getData(geraetMenge);
            crs.next();
            anzahlGeraete = crs.getInt(1);
            assertEquals(0, anzahlGeraete);
            assert true;
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testUpdateGeraet() {
        try {
            //Testobjekte Erzeugen
            Raum raum = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
            Raum raum2 = new Raum(UUID.fromString("04c18f4e-36db-4f65-816c-475577a044a2"), "Raum 3");
            Lampe lampe = (Lampe) geraetFactory.createGeraet(UUID.fromString("fe4dacb0-7ef9-405f-be51-b739a4b6cd29"),
                    "Lampe 1", raum2, "Lampe");
            Map<String, String> lampeMap = new HashMap<>();
            lampeMap.put("haelligkeit", "99.7");
            lampeMap.put("farbe", "#00FF88");
            lampeMap.put("eingeschaltet", "true");
            Map<String, String> lampeMap2 = new HashMap<>();
            lampeMap2.put("haelligkeit", "50.12");
            lampeMap2.put("farbe", "#1234AB");
            lampeMap2.put("eingeschaltet", "false");



            //Überprüfen, dass leer
            CachedRowSet crs = dataAccess.getData(geraetMenge);
            crs.next();
            int anzahlGeraete = crs.getInt(1);
            assertEquals(0, anzahlGeraete);

            //einfügen
            raumDataService.addRaum(raum);
            raumDataService.addRaum(raum2);
            geraetDataService.addGeraet(lampe, "Lampe", lampeMap);

            //Prüfen, dass 1 Geräte in Datenbank ist
            crs = dataAccess.getData(geraetMenge);
            crs.next();
            anzahlGeraete = crs.getInt(1);
            assertEquals(1, anzahlGeraete);

            //language=SQL
            String sql = """
                    SELECT GERAETE.ID, NAME, RAUM, ART, SCHLUESSEL, WERT
                    FROM GERAETE
                    JOIN GERAETE_WERTE
                    ON GERAETE.ID = GERAETE_WERTE.GERAET
                    ORDER BY GERAETE.ID
                    """;
            crs = dataAccess.getData(sql);
            while (crs.next()) {
                assertEquals(crs.getObject(1), lampe.getId());
                assertEquals(crs.getString(2), lampe.getName());
                assertEquals(crs.getObject(3), raum2.getId());
                assertEquals("Lampe", crs.getObject(4));
                assertEquals(crs.getString(6), lampeMap.get(crs.getString(5)));
            }

            geraetDataService.updateGeraetName(lampe, "Lampe 2");
            geraetDataService.updateGeraetRaum(lampe, raum);
            geraetDataService.updateGeraetWerte(lampe, lampeMap2);

            crs = dataAccess.getData(sql);
            while (crs.next()) {
                assertEquals(crs.getObject(1), lampe.getId());
                assertEquals("Lampe 2", crs.getString(2));
                assertEquals(crs.getObject(3), raum.getId());
                assertEquals("Lampe", crs.getObject(4));
                assertEquals(crs.getString(6), lampeMap2.get(crs.getString(5)));
            }
        } catch (Exception e) {
            assert false;
        }
    }

    @AfterEach
    void cleanUp() {
        try {
            //language=SQL
            dataAccess.executeTestUpdate("DELETE FROM GERAETE_WERTE");
            //language=SQL
            dataAccess.executeTestUpdate("DELETE FROM GERAETE");
            //language=SQL
            dataAccess.executeTestUpdate("DELETE FROM RAEUME");
        } catch (SQLException e) {
            assert false;
        }
    }
}
