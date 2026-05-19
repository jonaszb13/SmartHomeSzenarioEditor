package unit.services;

import data.models.fachobjekte.GeraetFactory;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import data.models.fachobjekte.geraeteArten.Sensor;
import data.services.datenServices.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SzenarioDataServiceTest {
    static final String SENSOR = "Sensor";
    static final String SZENARIO_1 = "Szenario 1";
    static final String SZENARIO_2 = "Szenario 2";
    static final String SENSOR_AN = "Sensor an";
    static final String SENSOR_AUS = "Sensor aus";
    static final String EINGESCHALTET = "eingeschaltet";
    static final String TRUE = "true";
    static final String FALSE = "false";
    static final String RAUM_1 = "Raum 1";
    static final String SENSOR_1 = "Sensor 1";
    static final String UUID_1 = "9bf21849-af67-4c50-ba0d-6e991850ceb4";
    static final String UUID_2 = "c216e129-1541-4455-804c-411b17dd015b";
    static final String UUID_3 = "c06ecee9-65c3-4444-aa82-e1148badfc0d";
    static final String UUID_4 = "e41cbbba-759f-4e11-9de8-04d5e941da5b";
    private static DataAccess dataAccess;
    private static RaumDataService raumDataService;
    private static GeraetDataService geraetDataService;
    private static SzenarioDataService szenarioDataService;
    private static GeraetFactory geraetFactory;
    //language=SQL
    static final String SZENARIO_MENGE = "SELECT COUNT(*) FROM SZENARIEN";

    @BeforeAll
    static void setup() throws SQLException {
        DataAccess.setTest(true);
        DatabaseCreationService.createDatabase();
        dataAccess = DataAccess.getInstance();
        raumDataService = RaumDataService.getInstance();
        geraetDataService = GeraetDataService.getInstance();
        szenarioDataService = SzenarioDataService.getInstance();
        geraetFactory = GeraetFactory.getInstance();
    }

    @Test
    void testAddSzenario() throws Exception {
        Raum raum = new Raum(java.util.UUID.fromString(UUID_1), RAUM_1);
        Sensor sensor = (Sensor) geraetFactory.createGeraet(java.util.UUID.fromString(UUID_2),
                SENSOR_1, raum, SENSOR);
        Szenario szenario1 = new Szenario(java.util.UUID.fromString(UUID_3), SZENARIO_1);
        szenario1.setBeschreibung(SZENARIO_1);
        Szenario szenario2 = new Szenario(java.util.UUID.fromString("22710be6-8c78-404f-b89e-3d47b20c1db6"), SZENARIO_2);
        szenario2.setBeschreibung(SZENARIO_2);
        szenario1.getAenderungen().put(1, new Szenario.Aenderung(
                java.util.UUID.fromString(UUID_4), sensor, SENSOR_AN, EINGESCHALTET, TRUE));
        szenario2.getAenderungen().put(1, new Szenario.Aenderung(
                java.util.UUID.fromString("81abcdf3-c6dd-4ee4-8deb-1dd0b96d55e7"), sensor, SENSOR_AUS, EINGESCHALTET, FALSE));

        CachedRowSet crs = dataAccess.getData(SZENARIO_MENGE);
        crs.next();
        assertEquals(0, crs.getInt(1));

        raumDataService.addRaum(raum);
        geraetDataService.addGeraet(sensor, SENSOR, new HashMap<>());
        szenarioDataService.addSzenario(szenario1);
        szenarioDataService.addSzenario(szenario2);

        crs = dataAccess.getData(SZENARIO_MENGE);
        crs.next();
        assertEquals(2, crs.getInt(1));

        //language=SQL
        crs = dataAccess.getData("""
                SELECT SZENARIEN.ID, NAME, SZENARIEN_INHALT.ID, AKTION, GERAET, SCHLUESSEL, WERT, POSITION
                FROM SZENARIEN
                JOIN SZENARIEN_INHALT ON SZENARIEN.ID = SZENARIEN_INHALT.SZENARIO
                ORDER BY SZENARIEN.ID
                """);

        crs.next();
        assertEquals(szenario2.getId(), crs.getObject(1));
        assertEquals(szenario2.getBeschreibung(), crs.getString(2));
        assertEquals(szenario2.getAenderungen().get(crs.getInt(8)).id(), crs.getObject(3));
        assertEquals(szenario2.getAenderungen().get(crs.getInt(8)).beschreibung(), crs.getString(4));
        assertEquals(szenario2.getAenderungen().get(crs.getInt(8)).geraet().getId(), crs.getObject(5));
        assertEquals(szenario2.getAenderungen().get(crs.getInt(8)).schluessel(), crs.getString(6));
        assertEquals(szenario2.getAenderungen().get(crs.getInt(8)).wert(), crs.getString(7));

        crs.next();
        assertEquals(szenario1.getId(), crs.getObject(1));
        assertEquals(szenario1.getBeschreibung(), crs.getString(2));
        assertEquals(szenario1.getAenderungen().get(crs.getInt(8)).id(), crs.getObject(3));
        assertEquals(szenario1.getAenderungen().get(crs.getInt(8)).beschreibung(), crs.getString(4));
        assertEquals(szenario1.getAenderungen().get(crs.getInt(8)).geraet().getId(), crs.getObject(5));
        assertEquals(szenario1.getAenderungen().get(crs.getInt(8)).schluessel(), crs.getString(6));
        assertEquals(szenario1.getAenderungen().get(crs.getInt(8)).wert(), crs.getString(7));
    }

    @Test
    void testUpdateSzenario() throws Exception {
        Raum raum = new Raum(java.util.UUID.fromString(UUID_1), RAUM_1);
        Sensor sensor = (Sensor) geraetFactory.createGeraet(java.util.UUID.fromString(UUID_2),
                SENSOR_1, raum, SENSOR);
        Szenario szenario = new Szenario(java.util.UUID.fromString(UUID_3), SZENARIO_1);
        szenario.setBeschreibung(SZENARIO_1);
        szenario.getAenderungen().put(1, new Szenario.Aenderung(
                java.util.UUID.fromString(UUID_4), sensor, SENSOR_AN, EINGESCHALTET, TRUE));

        raumDataService.addRaum(raum);
        geraetDataService.addGeraet(sensor, SENSOR, new HashMap<>());
        szenarioDataService.addSzenario(szenario);

        szenario.setName("Szenario 1 Neu");
        szenario.setBeschreibung("Szenario 1 Neu");
        szenarioDataService.updateSzenario(szenario);

        //language=SQL
        CachedRowSet crs = dataAccess.getData("""
                SELECT SZENARIEN.ID, NAME, SZENARIEN_INHALT.ID, AKTION, GERAET, SCHLUESSEL, WERT, POSITION
                FROM SZENARIEN
                JOIN SZENARIEN_INHALT ON SZENARIEN.ID = SZENARIEN_INHALT.SZENARIO
                ORDER BY SZENARIEN.ID
                """);
        crs.next();
        assertEquals(szenario.getId(), crs.getObject(1));
        assertEquals("Szenario 1 Neu", crs.getString(2));
        assertEquals(szenario.getAenderungen().get(crs.getInt(8)).id(), crs.getObject(3));
    }

    @Test
    void testUpdateSzenarioStatus() throws Exception {
        Raum raum = new Raum(java.util.UUID.fromString(UUID_1), RAUM_1);
        Sensor sensor = (Sensor) geraetFactory.createGeraet(java.util.UUID.fromString(UUID_2),
                SENSOR_1, raum, SENSOR);
        Map<String, String> sensorMap = new HashMap<>();
        sensorMap.put(EINGESCHALTET, FALSE);
        sensorMap.put("ausschlag", FALSE);
        Szenario szenario = new Szenario(java.util.UUID.fromString(UUID_3), SZENARIO_1);
        szenario.setBeschreibung(SZENARIO_1);
        szenario.getAenderungen().put(1, new Szenario.Aenderung(
                java.util.UUID.fromString(UUID_4), sensor, SENSOR_AN, EINGESCHALTET, TRUE));

        raumDataService.addRaum(raum);
        geraetDataService.addGeraet(sensor, SENSOR, sensorMap);
        szenarioDataService.addSzenario(szenario);

        //language=SQL
        CachedRowSet crs = dataAccess.getData("SELECT ID, NAME, STATUS FROM SZENARIEN");
        crs.next();
        assertEquals(szenario.getId(), crs.getObject(1));
        assertEquals(FALSE, crs.getString(3));

        szenarioDataService.updateSzenarioStatus(szenario, true);
        crs = dataAccess.getData("SELECT ID, NAME, STATUS FROM SZENARIEN");
        crs.next();
        assertEquals(TRUE, crs.getString(3));

        szenarioDataService.updateSzenarioStatus(szenario, false);
        crs = dataAccess.getData("SELECT ID, NAME, STATUS FROM SZENARIEN");
        crs.next();
        assertEquals(FALSE, crs.getString(3));
    }

    @Test
    void testDeleteSzenario() throws Exception {
        Raum raum = new Raum(java.util.UUID.fromString(UUID_1), RAUM_1);
        Sensor sensor = (Sensor) geraetFactory.createGeraet(java.util.UUID.fromString(UUID_2),
                SENSOR_1, raum, SENSOR);
        Szenario szenario1 = new Szenario(java.util.UUID.fromString(UUID_3), SZENARIO_1);
        szenario1.setBeschreibung(SZENARIO_1);
        Szenario szenario2 = new Szenario(java.util.UUID.fromString("22710be6-8c78-404f-b89e-3d47b20c1db6"), SZENARIO_2);
        szenario2.setBeschreibung(SZENARIO_2);
        szenario1.getAenderungen().put(1, new Szenario.Aenderung(
                java.util.UUID.fromString(UUID_4), sensor, SENSOR_AN, EINGESCHALTET, TRUE));
        szenario2.getAenderungen().put(1, new Szenario.Aenderung(
                java.util.UUID.fromString("81abcdf3-c6dd-4ee4-8deb-1dd0b96d55e7"), sensor, SENSOR_AUS, EINGESCHALTET, FALSE));

        raumDataService.addRaum(raum);
        geraetDataService.addGeraet(sensor, SENSOR, new HashMap<>());
        szenarioDataService.addSzenario(szenario1);
        szenarioDataService.addSzenario(szenario2);

        szenarioDataService.deleteSzenario(szenario1);
        CachedRowSet crs = dataAccess.getData(SZENARIO_MENGE);
        crs.next();
        assertEquals(1, crs.getInt(1));

        szenarioDataService.deleteSzenario(szenario2);
        crs = dataAccess.getData(SZENARIO_MENGE);
        crs.next();
        assertEquals(0, crs.getInt(1));
    }

    @Test
    void testAddSzenarioInhalt() throws Exception {
        Raum raum = new Raum(java.util.UUID.fromString(UUID_1), RAUM_1);
        Sensor sensor = (Sensor) geraetFactory.createGeraet(java.util.UUID.fromString(UUID_2),
                SENSOR_1, raum, SENSOR);
        Szenario szenario = new Szenario(java.util.UUID.fromString(UUID_3), SZENARIO_1);
        szenario.setBeschreibung(SZENARIO_1);
        szenario.getAenderungen().put(1, new Szenario.Aenderung(
                java.util.UUID.fromString(UUID_4), sensor, SENSOR_AN, EINGESCHALTET, TRUE));
        Szenario.Aenderung neueAenderung = new Szenario.Aenderung(
                java.util.UUID.fromString("b64095bb-25ea-4c60-ac63-d8a4c7f0158d"), sensor, SENSOR_AUS, EINGESCHALTET, FALSE);

        raumDataService.addRaum(raum);
        geraetDataService.addGeraet(sensor, SENSOR, new HashMap<>());
        szenarioDataService.addSzenario(szenario);
        szenarioDataService.addSzenarioInhalt(szenario, neueAenderung, 2);
        szenario.getAenderungen().put(2, neueAenderung);

        //language=SQL
        CachedRowSet crs = dataAccess.getData("""
                SELECT SZENARIEN.ID, NAME, SZENARIEN_INHALT.ID, AKTION, GERAET, SCHLUESSEL, WERT, POSITION
                FROM SZENARIEN
                JOIN SZENARIEN_INHALT ON SZENARIEN.ID = SZENARIEN_INHALT.SZENARIO
                ORDER BY SZENARIEN_INHALT.ID
                """);
        while (crs.next()) {
            assertEquals(szenario.getId(), crs.getObject(1));
            assertEquals(szenario.getAenderungen().get(crs.getInt(8)).id(), crs.getObject(3));
            assertEquals(szenario.getAenderungen().get(crs.getInt(8)).wert(), crs.getString(7));
        }
    }

    @Test
    void testUpdateSzenarioInhalt() throws Exception {
        Raum raum = new Raum(java.util.UUID.fromString(UUID_1), RAUM_1);
        Sensor sensor = (Sensor) geraetFactory.createGeraet(java.util.UUID.fromString(UUID_2),
                SENSOR_1, raum, SENSOR);
        Szenario szenario = new Szenario(java.util.UUID.fromString(UUID_3), SZENARIO_1);
        szenario.setBeschreibung(SZENARIO_1);
        szenario.getAenderungen().put(1, new Szenario.Aenderung(
                java.util.UUID.fromString(UUID_4), sensor, SENSOR_AN, EINGESCHALTET, TRUE));
        // Gleiche ID, aber andere Beschreibung und anderer Wert
        Szenario.Aenderung aenderungNeu = new Szenario.Aenderung(
                java.util.UUID.fromString(UUID_4), sensor, SENSOR_AUS, EINGESCHALTET, FALSE);

        raumDataService.addRaum(raum);
        geraetDataService.addGeraet(sensor, SENSOR, new HashMap<>());
        szenarioDataService.addSzenario(szenario);

        szenarioDataService.alterSzenarioInhalt(aenderungNeu, 1);

        //language=SQL
        CachedRowSet crs = dataAccess.getData("""
                SELECT SZENARIEN_INHALT.ID, AKTION, SCHLUESSEL, WERT
                FROM SZENARIEN_INHALT
                """);
        crs.next();
        assertEquals(aenderungNeu.id(), crs.getObject(1));
        assertEquals(SENSOR_AUS, crs.getString(2));
        assertEquals(EINGESCHALTET, crs.getString(3));
        assertEquals(FALSE, crs.getString(4));
    }

    @Test
    void testDeleteSzenarioInhalt() throws Exception {
        Raum raum = new Raum(java.util.UUID.fromString(UUID_1), RAUM_1);
        Sensor sensor = (Sensor) geraetFactory.createGeraet(java.util.UUID.fromString(UUID_2),
                SENSOR_1, raum, SENSOR);
        Szenario szenario = new Szenario(java.util.UUID.fromString(UUID_3), SZENARIO_1);
        szenario.setBeschreibung(SZENARIO_1);
        Szenario.Aenderung aenderung = new Szenario.Aenderung(
                java.util.UUID.fromString("b64095bb-25ea-4c60-ac63-d8a4c7f0158d"), sensor, SENSOR_AN, EINGESCHALTET, TRUE);
        szenario.getAenderungen().put(1, aenderung);

        raumDataService.addRaum(raum);
        geraetDataService.addGeraet(sensor, SENSOR, new HashMap<>());
        szenarioDataService.addSzenario(szenario);

        szenarioDataService.deleteSzenarioInhalt(aenderung.id());

        //language=SQL
        CachedRowSet crs = dataAccess.getData("SELECT COUNT(*) FROM SZENARIEN_INHALT");
        crs.next();
        assertEquals(0, crs.getInt(1));
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
