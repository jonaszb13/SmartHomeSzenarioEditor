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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SzenarioDataServiceTest {
    static DataAccess dataAccess;
    static RaumDataService raumDataService;
    static GeraetDataService geraetDataService;
    static SzenarioDataService szenarioDataService;
    static GeraetFactory geraetFactory;
    //language=SQL
    static final String szenarioMenge = """
            SELECT COUNT(*)
            FROM SZENARIEN;
            """;

    @BeforeAll
    static void setup() {
        DataAccess.setTest(true);
        try {
            DatabaseCreationService.createDatabase();
            dataAccess = DataAccess.getInstance();
            raumDataService = RaumDataService.getInstance();
            geraetDataService = GeraetDataService.getInstance();
            szenarioDataService = SzenarioDataService.getInstance();
            geraetFactory = GeraetFactory.getInstance();
        } catch (SQLException e) {
            assert false;
        }
    }

    @Test
    void testAddSzenario() {
        try {
            //Anlegen Test-Objekte
            Raum raum = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
            Sensor sensor = (Sensor) geraetFactory.createGeraet(UUID.fromString("c216e129-1541-4455-804c-411b17dd015b"),
                    "Sensor 1", raum, "Sensor");
            Szenario szenario1 = new Szenario(UUID.fromString("c06ecee9-65c3-4444-aa82-e1148badfc0d"), "Szenario 1");
            szenario1.setBeschreibung("Szenario 1");
            Szenario szenario2 = new Szenario(UUID.fromString("22710be6-8c78-404f-b89e-3d47b20c1db6"), "Szenario 2");
            szenario2.setBeschreibung("Szenario 2");
            szenario1.getAenderungen().put(1, new Szenario.Aenderung(
                    UUID.fromString("e41cbbba-759f-4e11-9de8-04d5e941da5b"), sensor, "Sensor an", "eingeschaltet", "true"));
            szenario2.getAenderungen().put(1, new Szenario.Aenderung(
                    UUID.fromString("81abcdf3-c6dd-4ee4-8deb-1dd0b96d55e7"), sensor, "Sensor aus", "eingeschaltet", "false"));

            //Testen, dass leer
            CachedRowSet crs = dataAccess.getData(szenarioMenge);
            crs.next();
            int anzahlSzenarien = crs.getInt(1);
            assertEquals(0, anzahlSzenarien);

            //Einfügen
            raumDataService.addRaum(raum);
            geraetDataService.addGeraet(sensor, "Sensor", new HashMap<>());
            szenarioDataService.addSzenario(szenario1);
            szenarioDataService.addSzenario(szenario2);

            //Testen
            crs = dataAccess.getData(szenarioMenge);
            crs.next();
            anzahlSzenarien = crs.getInt(1);
            assertEquals(2, anzahlSzenarien);

            //language=SQL
            crs = dataAccess.getData("""
                    SELECT SZENARIEN.ID, NAME, SZENARIEN_INHALT.ID, AKTION, GERAET, SCHLUESSEL, WERT, POSITION
                    FROM SZENARIEN
                    JOIN SZENARIEN_INHALT
                    ON SZENARIEN.ID = SZENARIEN_INHALT.SZENARIO
                    ORDER BY SZENARIEN.ID
                    """);

            crs.next();
            assertEquals(crs.getObject(1), szenario2.getId());
            assertEquals(crs.getString(2), szenario2.getBeschreibung());
            assertEquals(crs.getObject(3), szenario2.getAenderungen().get(crs.getInt(8)).id());
            assertEquals(crs.getString(4), szenario2.getAenderungen().get(crs.getInt(8)).beschreibung());
            assertEquals(crs.getObject(5), szenario2.getAenderungen().get(crs.getInt(8)).geraet().getId());
            assertEquals(crs.getString(6), szenario2.getAenderungen().get(crs.getInt(8)).schluessel());
            assertEquals(crs.getString(7), szenario2.getAenderungen().get(crs.getInt(8)).wert());

            crs.next();
            assertEquals(crs.getObject(1), szenario1.getId());
            assertEquals(crs.getString(2), szenario1.getBeschreibung());
            assertEquals(crs.getObject(3), szenario1.getAenderungen().get(crs.getInt(8)).id());
            assertEquals(crs.getString(4), szenario1.getAenderungen().get(crs.getInt(8)).beschreibung());
            assertEquals(crs.getObject(5), szenario1.getAenderungen().get(crs.getInt(8)).geraet().getId());
            assertEquals(crs.getString(6), szenario1.getAenderungen().get(crs.getInt(8)).schluessel());
            assertEquals(crs.getString(7), szenario1.getAenderungen().get(crs.getInt(8)).wert());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void updateSzenario() {
        try {
            //Anlegen Test-Objekte
            Raum raum = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
            Sensor sensor = (Sensor) geraetFactory.createGeraet(UUID.fromString("c216e129-1541-4455-804c-411b17dd015b"),
                    "Sensor 1", raum, "Sensor");
            Szenario szenario = new Szenario(UUID.fromString("c06ecee9-65c3-4444-aa82-e1148badfc0d"), "Szenario 1");
            szenario.setBeschreibung("Szenario 1");
            szenario.getAenderungen().put(1, new Szenario.Aenderung(
                    UUID.fromString("e41cbbba-759f-4e11-9de8-04d5e941da5b"), sensor, "Sensor an", "eingeschaltet", "true"));

            //Testen, dass leer
            CachedRowSet crs = dataAccess.getData(szenarioMenge);
            crs.next();
            int anzahlSzenarien = crs.getInt(1);
            assertEquals(0, anzahlSzenarien);

            //Einfügen
            raumDataService.addRaum(raum);
            geraetDataService.addGeraet(sensor, "Sensor", new HashMap<>());
            szenarioDataService.addSzenario(szenario);

            //Testen
            crs = dataAccess.getData(szenarioMenge);
            crs.next();
            anzahlSzenarien = crs.getInt(1);
            assertEquals(1, anzahlSzenarien);

            //language=SQL
            crs = dataAccess.getData("""
                    SELECT SZENARIEN.ID, NAME, SZENARIEN_INHALT.ID, AKTION, GERAET, SCHLUESSEL, WERT, POSITION
                    FROM SZENARIEN
                    JOIN SZENARIEN_INHALT
                    ON SZENARIEN.ID = SZENARIEN_INHALT.SZENARIO
                    ORDER BY SZENARIEN.ID
                    """);

            crs.next();
            assertEquals(crs.getObject(1), szenario.getId());
            assertEquals(crs.getString(2), szenario.getBeschreibung());
            assertEquals(crs.getObject(3), szenario.getAenderungen().get(crs.getInt(8)).id());
            assertEquals(crs.getString(4), szenario.getAenderungen().get(crs.getInt(8)).beschreibung());
            assertEquals(crs.getObject(5), szenario.getAenderungen().get(crs.getInt(8)).geraet().getId());
            assertEquals(crs.getString(6), szenario.getAenderungen().get(crs.getInt(8)).schluessel());
            assertEquals(crs.getString(7), szenario.getAenderungen().get(crs.getInt(8)).wert());

            //Aktualisierung
            szenario.setName("Szenario 1 Neu");
            szenario.setBeschreibung("Szenario 1 Neu");
            szenarioDataService.updateSzenario(szenario);

            //language=SQL
            crs = dataAccess.getData("""
                    SELECT SZENARIEN.ID, NAME, SZENARIEN_INHALT.ID, AKTION, GERAET, SCHLUESSEL, WERT, POSITION
                    FROM SZENARIEN
                    JOIN SZENARIEN_INHALT
                    ON SZENARIEN.ID = SZENARIEN_INHALT.SZENARIO
                    ORDER BY SZENARIEN.ID
                    """);

            crs.next();
            assertEquals(crs.getObject(1), szenario.getId());
            assertEquals(crs.getString(2), szenario.getBeschreibung());
            assertEquals(crs.getObject(3), szenario.getAenderungen().get(crs.getInt(8)).id());
            assertEquals(crs.getString(4), szenario.getAenderungen().get(crs.getInt(8)).beschreibung());
            assertEquals(crs.getObject(5), szenario.getAenderungen().get(crs.getInt(8)).geraet().getId());
            assertEquals(crs.getString(6), szenario.getAenderungen().get(crs.getInt(8)).schluessel());
            assertEquals(crs.getString(7), szenario.getAenderungen().get(crs.getInt(8)).wert());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void updateSzenarioStatusTest() {
        //TODO
        try {
            //Anlegen Test-Objekte
            Raum raum = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
            Sensor sensor = (Sensor) geraetFactory.createGeraet(UUID.fromString("c216e129-1541-4455-804c-411b17dd015b"),
                    "Sensor 1", raum, "Sensor");
            Map<String, String> sensorMap = new HashMap<>();
            sensorMap.put("eingeschaltet", "false");
            sensorMap.put("ausschlag", "false");
            Szenario szenario = new Szenario(UUID.fromString("c06ecee9-65c3-4444-aa82-e1148badfc0d"), "Szenario 1");
            szenario.setBeschreibung("Szenario 1");
            szenario.getAenderungen().put(1, new Szenario.Aenderung(
                    UUID.fromString("e41cbbba-759f-4e11-9de8-04d5e941da5b"), sensor, "Sensor an", "eingeschaltet", "true"));

            //Testen, dass leer
            CachedRowSet crs = dataAccess.getData(szenarioMenge);
            crs.next();
            int anzahlSzenarien = crs.getInt(1);
            assertEquals(0, anzahlSzenarien);

            //Einfügen
            raumDataService.addRaum(raum);
            geraetDataService.addGeraet(sensor, "Sensor", sensorMap);
            szenarioDataService.addSzenario(szenario);

            //Testen des Ursprungszustands

            //language=SQL
            crs = dataAccess.getData("""
                    SELECT ID, NAME, STATUS
                    FROM SZENARIEN
                    """);
            crs.next();
            assertEquals(crs.getObject(1), szenario.getId());
            assertEquals(crs.getString(2), szenario.getName());
            assertEquals("false", crs.getString(3));

            //Test 1
            szenarioDataService.updateSzenarioStatus(szenario, true);

            //Überprüfung
            //language=SQL
            crs = dataAccess.getData("""
                    SELECT ID, NAME, STATUS
                    FROM SZENARIEN
                    """);
            crs.next();
            assertEquals(crs.getObject(1), szenario.getId());
            assertEquals(crs.getString(2), szenario.getName());
            assertEquals("true", crs.getString(3));

            //Test 2
            szenarioDataService.updateSzenarioStatus(szenario, false);

            //language=SQL
            crs = dataAccess.getData("""
                    SELECT ID, NAME, STATUS
                    FROM SZENARIEN
                    """);
            crs.next();
            assertEquals(crs.getObject(1), szenario.getId());
            assertEquals(crs.getString(2), szenario.getName());
            assertEquals("false", crs.getString(3));
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testDeleteSzenario() {
        try {
            //Anlegen Test-Objekte
            Raum raum = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
            Sensor sensor = (Sensor) geraetFactory.createGeraet(UUID.fromString("c216e129-1541-4455-804c-411b17dd015b"),
                    "Sensor 1", raum, "Sensor");
            Szenario szenario1 = new Szenario(UUID.fromString("c06ecee9-65c3-4444-aa82-e1148badfc0d"), "Szenario 1");
            szenario1.setBeschreibung("Szenario 1");
            Szenario szenario2 = new Szenario(UUID.fromString("22710be6-8c78-404f-b89e-3d47b20c1db6"), "Szenario 2");
            szenario2.setBeschreibung("Szenario 2");
            szenario1.getAenderungen().put(1, new Szenario.Aenderung(
                    UUID.fromString("e41cbbba-759f-4e11-9de8-04d5e941da5b"), sensor, "Sensor an", "eingeschaltet", "true"));
            szenario2.getAenderungen().put(1, new Szenario.Aenderung(
                    UUID.fromString("81abcdf3-c6dd-4ee4-8deb-1dd0b96d55e7"), sensor, "Sensor aus", "eingeschaltet", "false"));

            //Einfügen
            raumDataService.addRaum(raum);
            geraetDataService.addGeraet(sensor, "Sensor", new HashMap<>());
            szenarioDataService.addSzenario(szenario1);
            szenarioDataService.addSzenario(szenario2);

            //Testen
            CachedRowSet crs = dataAccess.getData(szenarioMenge);
            crs.next();
            int anzahlSzenarien = crs.getInt(1);
            assertEquals(2, anzahlSzenarien);

            szenarioDataService.deleteSzenario(szenario1);

            crs = dataAccess.getData(szenarioMenge);
            crs.next();
            anzahlSzenarien = crs.getInt(1);
            assertEquals(1, anzahlSzenarien);

            szenarioDataService.deleteSzenario(szenario2);

            crs = dataAccess.getData(szenarioMenge);
            crs.next();
            anzahlSzenarien = crs.getInt(1);
            assertEquals(0, anzahlSzenarien);
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testAddSzenarioInhalt() {
        try {
            //Anlegen Test-Objekte
            Raum raum = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
            Sensor sensor1 = (Sensor) geraetFactory.createGeraet(UUID.fromString("c216e129-1541-4455-804c-411b17dd015b"),
                    "Sensor 1", raum, "Sensor");
            Szenario szenario = new Szenario(UUID.fromString("c06ecee9-65c3-4444-aa82-e1148badfc0d"), "Szenario 1");
            szenario.setBeschreibung("Szenario 1");
            szenario.getAenderungen().put(1, new Szenario.Aenderung(
                    UUID.fromString("e41cbbba-759f-4e11-9de8-04d5e941da5b"), sensor1, "Sensor an", "eingeschaltet", "true"));
            Szenario.Aenderung aenderung = new Szenario.Aenderung(
                    UUID.fromString("b64095bb-25ea-4c60-ac63-d8a4c7f0158d"), sensor1, "Sensor an", "eingeschaltet", "true");


            //Testen, dass leer
            CachedRowSet crs = dataAccess.getData(szenarioMenge);
            crs.next();
            int anzahlSzenarien = crs.getInt(1);
            assertEquals(0, anzahlSzenarien);

            //Einfügen
            raumDataService.addRaum(raum);
            geraetDataService.addGeraet(sensor1, "Sensor", new HashMap<>());
            szenarioDataService.addSzenario(szenario);

            //Testen
            //language=SQL
            crs = dataAccess.getData("""
                    SELECT SZENARIEN.ID, NAME, SZENARIEN_INHALT.ID, AKTION, GERAET, SCHLUESSEL, WERT, POSITION
                    FROM SZENARIEN
                    JOIN SZENARIEN_INHALT
                    ON SZENARIEN.ID = SZENARIEN_INHALT.SZENARIO
                    """);

            crs.next();
            assertEquals(crs.getObject(1), szenario.getId());
            assertEquals(crs.getString(2), szenario.getBeschreibung());
            assertEquals(crs.getObject(3), szenario.getAenderungen().get(crs.getInt(8)).id());
            assertEquals(crs.getString(4), szenario.getAenderungen().get(crs.getInt(8)).beschreibung());
            assertEquals(crs.getObject(5), szenario.getAenderungen().get(crs.getInt(8)).geraet().getId());
            assertEquals(crs.getString(6), szenario.getAenderungen().get(crs.getInt(8)).schluessel());
            assertEquals(crs.getString(7), szenario.getAenderungen().get(crs.getInt(8)).wert());

            szenarioDataService.addSzenarioInhalt(szenario, aenderung, 2);
            szenario.getAenderungen().put(2, aenderung);

            //language=SQL
            crs = dataAccess.getData("""
                    SELECT SZENARIEN.ID, NAME, SZENARIEN_INHALT.ID, AKTION, GERAET, SCHLUESSEL, WERT, POSITION
                    FROM SZENARIEN
                    JOIN SZENARIEN_INHALT
                    ON SZENARIEN.ID = SZENARIEN_INHALT.SZENARIO
                    ORDER BY SZENARIEN_INHALT.ID
                    """);

            while (crs.next()) {
                assertEquals(crs.getObject(1), szenario.getId());
                assertEquals(crs.getString(2), szenario.getBeschreibung());
                assertEquals(crs.getObject(3), szenario.getAenderungen().get(crs.getInt(8)).id());
                assertEquals(crs.getString(4), szenario.getAenderungen().get(crs.getInt(8)).beschreibung());
                assertEquals(crs.getObject(5), szenario.getAenderungen().get(crs.getInt(8)).geraet().getId());
                assertEquals(crs.getString(6), szenario.getAenderungen().get(crs.getInt(8)).schluessel());
                assertEquals(crs.getString(7), szenario.getAenderungen().get(crs.getInt(8)).wert());
            }
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testUpdateSzenarioInhalt() {
        try {
            //Anlegen Test-Objekte
            Raum raum = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
            Sensor sensor1 = (Sensor) geraetFactory.createGeraet(UUID.fromString("c216e129-1541-4455-804c-411b17dd015b"),
                    "Sensor 1", raum, "Sensor");
            Szenario szenario = new Szenario(UUID.fromString("c06ecee9-65c3-4444-aa82-e1148badfc0d"), "Szenario 1");
            szenario.setBeschreibung("Szenario 1");
            szenario.getAenderungen().put(1, new Szenario.Aenderung(
                    UUID.fromString("e41cbbba-759f-4e11-9de8-04d5e941da5b"), sensor1, "Sensor an", "eingeschaltet", "true"));
            Szenario.Aenderung aenderungNeu = new Szenario.Aenderung(
                    UUID.fromString("e41cbbba-759f-4e11-9de8-04d5e941da5b"), sensor1, "Sensor an", "eingeschaltet", "true");


            //Testen, dass leer
            CachedRowSet crs = dataAccess.getData(szenarioMenge);
            crs.next();
            int anzahlSzenarien = crs.getInt(1);
            assertEquals(0, anzahlSzenarien);

            //Einfügen
            raumDataService.addRaum(raum);
            geraetDataService.addGeraet(sensor1, "Sensor", new HashMap<>());
            szenarioDataService.addSzenario(szenario);

            //Testen
            //language=SQL
            crs = dataAccess.getData("""
                    SELECT SZENARIEN.ID, NAME, SZENARIEN_INHALT.ID, AKTION, GERAET, SCHLUESSEL, WERT, POSITION
                    FROM SZENARIEN
                    JOIN SZENARIEN_INHALT
                    ON SZENARIEN.ID = SZENARIEN_INHALT.SZENARIO
                    """);

            crs.next();
            assertEquals(crs.getObject(1), szenario.getId());
            assertEquals(crs.getString(2), szenario.getBeschreibung());
            assertEquals(crs.getObject(3), szenario.getAenderungen().get(crs.getInt(8)).id());
            assertEquals(crs.getString(4), szenario.getAenderungen().get(crs.getInt(8)).beschreibung());
            assertEquals(crs.getObject(5), szenario.getAenderungen().get(crs.getInt(8)).geraet().getId());
            assertEquals(crs.getString(6), szenario.getAenderungen().get(crs.getInt(8)).schluessel());
            assertEquals(crs.getString(7), szenario.getAenderungen().get(crs.getInt(8)).wert());

            szenarioDataService.alterSzenarioInhalt(aenderungNeu, 1);

            //language=SQL
            crs = dataAccess.getData("""
                    SELECT SZENARIEN.ID, NAME, SZENARIEN_INHALT.ID, AKTION, GERAET, SCHLUESSEL, WERT, POSITION
                    FROM SZENARIEN
                    JOIN SZENARIEN_INHALT
                    ON SZENARIEN.ID = SZENARIEN_INHALT.SZENARIO
                    ORDER BY SZENARIEN_INHALT.ID
                    """);

            while (crs.next()) {
                assertEquals(crs.getObject(1), szenario.getId());
                assertEquals(crs.getString(2), szenario.getBeschreibung());
                assertEquals(crs.getObject(3), aenderungNeu.id());
                assertEquals(crs.getString(4), aenderungNeu.beschreibung());
                assertEquals(crs.getObject(5), aenderungNeu.geraet().getId());
                assertEquals(crs.getString(6), aenderungNeu.schluessel());
                assertEquals(crs.getString(7), aenderungNeu.wert());
            }
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void testDeleteSzenarioInhalt() {
        try {
            //Anlegen Test-Objekte
            Raum raum = new Raum(UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4"), "Raum 1");
            Sensor sensor1 = (Sensor) geraetFactory.createGeraet(UUID.fromString("c216e129-1541-4455-804c-411b17dd015b"),
                    "Sensor 1", raum, "Sensor");
            Szenario szenario = new Szenario(UUID.fromString("c06ecee9-65c3-4444-aa82-e1148badfc0d"), "Szenario 1");
            szenario.setBeschreibung("Szenario 1");
            Szenario.Aenderung aenderung = new Szenario.Aenderung(
                    UUID.fromString("b64095bb-25ea-4c60-ac63-d8a4c7f0158d"), sensor1, "Sensor an", "eingeschaltet", "true");
            szenario.getAenderungen().put(1, aenderung);

            //Einfügen
            raumDataService.addRaum(raum);
            geraetDataService.addGeraet(sensor1, "Sensor", new HashMap<>());
            szenarioDataService.addSzenario(szenario);

            //Vorbedingung
            //language=SQL
            CachedRowSet crs = dataAccess.getData("""
                    SELECT COUNT(*)
                    FROM SZENARIEN_INHALT
                    """);
            crs.next();
            int anzahlSzenarienInhalte = crs.getInt(1);
            assertEquals(1, anzahlSzenarienInhalte);

            //Löschen
            szenarioDataService.deleteSzenarioInhalt(aenderung.id());

            //Testen
            //language=SQL
            crs = dataAccess.getData("""
                    SELECT COUNT(*)
                    FROM SZENARIEN_INHALT
                    """);
            crs.next();
            anzahlSzenarienInhalte = crs.getInt(1);
            assertEquals(0, anzahlSzenarienInhalte);
        } catch (
                Exception e) {
            assert false;
        }
    }

    @AfterEach
    void cleanUp() {
        try {
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
        } catch (SQLException e) {
            assert false;
        }
    }
}
