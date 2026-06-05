package unit.services.datenservices;

import data.models.fachobjekte.Raum;
import data.services.datenservices.*;
import org.junit.jupiter.api.*;
import util.statusmeldungen.Meldung;
import util.statusmeldungen.StatusLog;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataTransportServiceTest {

    static final UUID RAUM_ID = UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4");
    static final UUID SENSOR_ID = UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4");
    static final UUID WERT_ID = UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4");
    static final UUID SZENARIO_ID = UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4");
    static final UUID AENDERUNG_ID = UUID.fromString("9bf21849-af67-4c50-ba0d-6e991850ceb4");
    static final Raum TESTRAUM = new Raum(RAUM_ID, "Testraum");

    private static DataAccess dataAccess;
    private static RaumDataService raumDataService;
    private DataTransportService service;

    @BeforeAll
    static void setUp() throws SQLException {
        DataAccess.setTest(true);
        DatabaseCreationService.createDatabase();
        dataAccess = DataAccess.getInstance();
        raumDataService = RaumDataService.getInstance();
    }

    @BeforeEach
    void init() {
        StatusLog.clear();
        service = new DataTransportService();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        dataAccess.executeUpdate("DELETE FROM SZENARIEN_INHALT");
        dataAccess.executeUpdate("DELETE FROM SZENARIEN");
        dataAccess.executeUpdate("DELETE FROM GERAETE_WERTE");
        dataAccess.executeUpdate("DELETE FROM GERAETE");
        dataAccess.executeUpdate("DELETE FROM RAEUME");
    }

    @Test
    void clearAllData_leertAlleTabeleln() throws Exception {
        raumDataService.addRaum(TESTRAUM);

        service.clearAllData();

        assertFalse(StatusLog.hasError());
        CachedRowSet crs = dataAccess.getData("SELECT * FROM RAEUME");
        assertFalse(crs.next());
    }

    @Test
    void exportData_mitRaum_schreibtKorrektenInhalt() throws Exception {
        raumDataService.addRaum(TESTRAUM);
        File tempFile = File.createTempFile("export_test", ".csv");
        tempFile.deleteOnExit();

        boolean result = service.exportData(tempFile);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains(RAUM_ID.toString()));
        assertTrue(content.contains("Testraum"));
    }

    @Test
    void exportData_leereDB_enthaeltNurTrenner() throws Exception {
        File tempFile = File.createTempFile("export_empty", ".csv");
        tempFile.deleteOnExit();

        boolean result = service.exportData(tempFile);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        String content = Files.readString(tempFile.toPath());
        assertEquals(";\n;\n;\n;\n;\n", content);
    }

    @Test
    void exportData_invaliderPfad_gibtFalseZurueck() {
        File invaliderPfad = new File("/kein/pfad/datei.csv");

        boolean result = service.exportData(invaliderPfad);

        assertFalse(result);
        assertTrue(StatusLog.hasError());
    }

    @Test
    void importData_gueltigeDatei_importiertRaeume() throws Exception {
        raumDataService.addRaum(TESTRAUM);
        File tempFile = File.createTempFile("import_test", ".csv");
        tempFile.deleteOnExit();
        service.exportData(tempFile);
        dataAccess.executeUpdate("DELETE FROM RAEUME");

        boolean result = service.importData(tempFile);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        CachedRowSet crs = dataAccess.getData("SELECT * FROM RAEUME WHERE ID='" + RAUM_ID + "'");
        assertTrue(crs.next());
        assertEquals("Testraum", crs.getString("NAME"));
    }

    @Test
    void importData_dateiNichtVorhanden_gibtFalseZurueck() {
        File nichtExistent = new File("/tmp/existiert_nicht_" + System.currentTimeMillis() + ".csv");

        boolean result = service.importData(nichtExistent);

        assertFalse(result);
        assertTrue(StatusLog.hasError());
    }

    @Test
    void importData_unbekannterAbschnitt_wirdIgnoriertOhneFehler() throws Exception {
        File tempFile = File.createTempFile("extra_section", ".csv");
        tempFile.deleteOnExit();
        try (FileWriter fw = new FileWriter(tempFile)) {
            fw.write(";\n;\n;\n;\n;\ndieseZeileWirdIgnoriert\n");
        }

        boolean result = service.importData(tempFile);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
    }

    @Test
    void exportImportRoundtrip_datenBleibenerhalten() throws Exception {
        raumDataService.addRaum(TESTRAUM);
        File tempFile = File.createTempFile("roundtrip", ".csv");
        tempFile.deleteOnExit();

        service.exportData(tempFile);
        service.importData(tempFile);

        assertFalse(StatusLog.hasError());
        CachedRowSet crs = dataAccess.getData("SELECT * FROM RAEUME WHERE ID='" + RAUM_ID + "'");
        assertTrue(crs.next());
        assertEquals("Testraum", crs.getString("NAME"));
    }

    @Test
    void importData_statusmeldungBeiDateifehler_istFehler() {
        File nichtExistent = new File("/nicht/da" + System.currentTimeMillis() + ".csv");

        service.importData(nichtExistent);

        List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        assertFalse(meldungen.isEmpty());
        assertTrue(meldungen.stream().anyMatch(Meldung::isError));
    }

    @Test
    void exportData_mitGeraetUndWert_enthaeltGeraetdaten() throws Exception {
        dataAccess.executeUpdate("INSERT INTO RAEUME (ID, NAME) VALUES ('" + RAUM_ID + "', 'Testraum')");
        dataAccess.executeUpdate("INSERT INTO GERAETE (ID, NAME, RAUM, ART) VALUES ('" + SENSOR_ID + "', 'Testsensor', '" + RAUM_ID + "', 'Sensor')");
        dataAccess.executeUpdate("INSERT INTO GERAETE_WERTE (ID, GERAET, SCHLUESSEL, WERT) VALUES ('" + WERT_ID + "', '" + SENSOR_ID + "', 'eingeschaltet', 'true')");
        File tempFile = File.createTempFile("geraet_export", ".csv");
        tempFile.deleteOnExit();

        boolean result = service.exportData(tempFile);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains(SENSOR_ID.toString()));
        assertTrue(content.contains("Testsensor"));
        assertTrue(content.contains("eingeschaltet"));
        assertTrue(content.contains("true"));
    }

    @Test
    void exportImportRoundtrip_mitGeraet_werteBleibenerhalten() throws Exception {
        dataAccess.executeUpdate("INSERT INTO RAEUME (ID, NAME) VALUES ('" + RAUM_ID + "', 'Testraum')");
        dataAccess.executeUpdate("INSERT INTO GERAETE (ID, NAME, RAUM, ART) VALUES ('" + SENSOR_ID + "', 'Testsensor', '" + RAUM_ID + "', 'Sensor')");
        dataAccess.executeUpdate("INSERT INTO GERAETE_WERTE (ID, GERAET, SCHLUESSEL, WERT) VALUES ('" + WERT_ID + "', '" + SENSOR_ID + "', 'eingeschaltet', 'true')");
        File tempFile = File.createTempFile("geraet_roundtrip", ".csv");
        tempFile.deleteOnExit();

        service.exportData(tempFile);
        service.importData(tempFile);

        assertFalse(StatusLog.hasError());
        CachedRowSet geraet = dataAccess.getData("SELECT * FROM GERAETE WHERE ID='" + SENSOR_ID + "'");
        assertTrue(geraet.next());
        assertEquals("Testsensor", geraet.getString("NAME"));
        CachedRowSet wert = dataAccess.getData("SELECT * FROM GERAETE_WERTE WHERE GERAET='" + SENSOR_ID + "'");
        assertTrue(wert.next());
        assertEquals("true", wert.getString("WERT"));
    }

    @Test
    void exportData_mitSzenarioUndInhalt_enthaeltSzenariodaten() throws Exception {
        dataAccess.executeUpdate("INSERT INTO RAEUME (ID, NAME) VALUES ('" + RAUM_ID + "', 'Testraum')");
        dataAccess.executeUpdate("INSERT INTO GERAETE (ID, NAME, RAUM, ART) VALUES ('" + SENSOR_ID + "', 'Testsensor', '" + RAUM_ID + "', 'Sensor')");
        dataAccess.executeUpdate("INSERT INTO SZENARIEN (ID, NAME, BESCHREIBUNG) VALUES ('" + SZENARIO_ID + "', 'Testszenario', 'Beschreibung')");
        dataAccess.executeUpdate("INSERT INTO SZENARIEN_INHALT (ID, AKTION, SZENARIO, GERAET, SCHLUESSEL, WERT, POSITION) VALUES ('" + AENDERUNG_ID + "', 'Sensor an', '" + SZENARIO_ID + "', '" + SENSOR_ID + "', 'eingeschaltet', 'true', 1)");
        File tempFile = File.createTempFile("szenario_export", ".csv");
        tempFile.deleteOnExit();

        boolean result = service.exportData(tempFile);

        assertTrue(result);
        assertFalse(StatusLog.hasError());
        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains(SZENARIO_ID.toString()));
        assertTrue(content.contains("Testszenario"));
        assertTrue(content.contains(AENDERUNG_ID.toString()));
        assertTrue(content.contains("Sensor an"));
    }

    @Test
    void exportImportRoundtrip_mitSzenario_inhaltBleibtErhalten() throws Exception {
        dataAccess.executeUpdate("INSERT INTO RAEUME (ID, NAME) VALUES ('" + RAUM_ID + "', 'Testraum')");
        dataAccess.executeUpdate("INSERT INTO GERAETE (ID, NAME, RAUM, ART) VALUES ('" + SENSOR_ID + "', 'Testsensor', '" + RAUM_ID + "', 'Sensor')");
        dataAccess.executeUpdate("INSERT INTO SZENARIEN (ID, NAME, BESCHREIBUNG) VALUES ('" + SZENARIO_ID + "', 'Testszenario', 'Beschreibung')");
        dataAccess.executeUpdate("INSERT INTO SZENARIEN_INHALT (ID, AKTION, SZENARIO, GERAET, SCHLUESSEL, WERT, POSITION) VALUES ('" + AENDERUNG_ID + "', 'Sensor an', '" + SZENARIO_ID + "', '" + SENSOR_ID + "', 'eingeschaltet', 'true', 1)");
        File tempFile = File.createTempFile("szenario_roundtrip", ".csv");
        tempFile.deleteOnExit();

        service.exportData(tempFile);
        service.importData(tempFile);

        assertFalse(StatusLog.hasError());
        CachedRowSet szenario = dataAccess.getData("SELECT * FROM SZENARIEN WHERE ID='" + SZENARIO_ID + "'");
        assertTrue(szenario.next());
        assertEquals("Testszenario", szenario.getString("NAME"));
        CachedRowSet inhalt = dataAccess.getData("SELECT * FROM SZENARIEN_INHALT WHERE SZENARIO='" + SZENARIO_ID + "'");
        assertTrue(inhalt.next());
        assertEquals("Sensor an", inhalt.getString("AKTION"));
    }
}
