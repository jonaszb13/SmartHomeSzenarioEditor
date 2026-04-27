package data;

import data.models.Geraet;
import data.models.Raum;
import data.models.Szenario;
import util.DebugLog;
import util.customExceptions.NoGeraetProvidedException;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Klasse die alle Interaktionen mit der persistenten Datenhaltung handhabt.
 *
 * @author Ben Knirsch
 */
public class DataAccess {
    private final Connection conn;

    /**
     * @param url      Pfad, in dem Datenbank angelegt werden soll
     * @param user     Benutzername des Standartnutzers
     * @param password Passwort des Standartnutzers
     * @throws SQLException Wird geworfen, wenn ungültige Werte übergeben werden
     *                      und Verbindung nicht hergestellt werden kann
     */
    public DataAccess(final String url, final String user, final String password) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:h2:file:" + url + ";AUTO_SERVER=TRUE", user, password);
    }

    public static void main(String[] args) {
        final String url = "./data/mydb";
        final String user = "sa";
        final String password = "";
        final Map<UUID, Raum> raumHashMap = new HashMap<>();
        final Map<UUID, Geraet> geraetHashMap = new HashMap<>();
        final Map<UUID, Szenario> szenarioHashMap = new HashMap<>();
        try {
            final DataAccess dataAccess = new DataAccess(url, user, password);
            dataAccess.setupDatabase();
            List<Class<?>> geraeteKlassen = GeraetTypHandler.getGeraeteKlassen();
            dataAccess.mapAllData(raumHashMap, geraetHashMap, szenarioHashMap);
        } catch (SQLException | NoGeraetProvidedException e) {
            e.printStackTrace();
            DebugLog.addError(e);
            DebugLog.createErrorFile();
        }
        System.out.println("");
    }

    /**
     * Methode, die die Datenbank zur persistenten speicherung der Zustände anlegt
     *
     * @throws SQLException wenn Fehler mit er Datenbankverbindung auftritt
     */
    public void setupDatabase() throws SQLException {
        DebugLog.addHinweis("Datenbank verbunden und ggf. neu angelegt");
        final Statement stmt = conn.createStatement();
        //Allgemeine Tabellen
        stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS Raeume (
                    id uuid PRIMARY KEY,
                    name VARCHAR(255) NOT NULL);
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Geraete
                    (
                    id uuid PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    Raum uuid REFERENCES RAEUME(id),
                    Art varchar(63) NOT NULL
                    );
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Szenarien (
                    id uuid PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    Rythmus VARCHAR(255),
                    Status VARCHAR(255),
                    Beschreibung VARCHAR(255)
                    );
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Szenarien_Inhalt (
                    id uuid PRIMARY KEY,
                    aktion VARCHAR(255) NOT NULL,
                    Szenario uuid REFERENCES Szenarien(id),
                    Geraet uuid REFERENCES Geraete(id),
                    schluessel VARCHAR(255) NOT NULL,
                    Wert VARCHAR(255),
                    Position INT
                    );
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Geraete_Werte (
                    id uuid PRIMARY KEY,
                    Geraet uuid REFERENCES Geraete(id),
                    schluessel VARCHAR(255) NOT NULL,
                    Wert VARCHAR(255)
                    )
                """);
        stmt.close();
    }


    /**
     * @param raumMap     Map in der alle Räume in der Datenbank
     *                    auf ihre IDs gemaped werden
     * @param geraetMap   Map in der alle Geräte in der Datenbank
     *                    mit ihren Attributen auf ihre IDs gemaped werden
     * @param szenarioMap Map in der alle Szenarien in der Datenbank
     *                    mit ihren Inhalten auf ihre IDs gemaped werden
     * @throws SQLException              wenn ein Fehler bei der Datenbankverbindung auftritt
     * @throws NoGeraetProvidedException tritt auf, wenn in dem Ordner geraete keine Klassen vorhanden sind
     */
    public void mapAllData(final Map<UUID, Raum> raumMap, final Map<UUID, Geraet> geraetMap, final Map<UUID, Szenario> szenarioMap) throws SQLException, NoGeraetProvidedException {
        mapAllRaeume(raumMap);
        mapAllGeraete(raumMap, geraetMap);
        mapAllSzenarien(geraetMap, szenarioMap);
    }

    public void mapAllRaeume(final Map<UUID, Raum> raumMap) throws SQLException {
        final Statement stmt = conn.createStatement();
        //Laden der Räume
        DebugLog.addHinweis("Beginne RäumeMap zu laden");
        final ResultSet rs = stmt.executeQuery("SELECT * FROM RAEUME");
        while (rs.next()) {
            final UUID id = UUID.fromString(rs.getString("id"));
            final String name = rs.getString("name");
            raumMap.put(id, new Raum(id, name));
        }
        DebugLog.addHinweis("RäumeMap erfolgreich geladen");
    }

    public void mapAllGeraete(final Map<UUID, Raum> raumMap, final Map<UUID, Geraet> geraetMap) throws SQLException, NoGeraetProvidedException {
        final Statement stmt = conn.createStatement();
        DebugLog.addHinweis("Beginne GeräteMap zu laden");
        final GeraetFactory gf = GeraetFactory.getInstance();
        //TODO QUESTION: Sollen geräte ohne Attribute geladen werden?
        final ResultSet rs = stmt.executeQuery("""
                SELECT GERAETE.ID, GERAETE.NAME, GERAETE.RAUM, Geraete.ART, SCHLUESSEL, WERT
                FROM Geraete
                JOIN GERAETE_WERTE ON Geraete.ID = GERAETE_WERTE.Geraet
                ORDER BY Geraete.ART, Geraete.ID
                """);
        Map<String, String> atributeHashMap = new HashMap<>();
        UUID lastId = null;
        Geraet aktuellesGeraet = null;
        boolean erstesMal = true;
        while (rs.next()) {
            final UUID id = UUID.fromString(rs.getString("id"));
            if (!id.equals(lastId)) {
                if (erstesMal) erstesMal = false;
                else {
                    aktuellesGeraet.setValues(atributeHashMap);
                    atributeHashMap = new HashMap<>();
                }
                final UUID raum = UUID.fromString(rs.getString("raum"));
                try {
                    aktuellesGeraet = gf.createGeraet(id, rs.getString("name"),
                            raumMap.get(raum), rs.getString("art"));
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    DebugLog.addError("Bei der dynamischen Erstellung eines Geräts ist ein Fehler aufgetreten", e);
                    //TODO was mit Null tun?
                }
                atributeHashMap.put(rs.getString("schluessel"), rs.getString("wert"));
                geraetMap.put(id, aktuellesGeraet);
                raumMap.get(raum).getGeraete().add(aktuellesGeraet);
                lastId = id;
            }
            atributeHashMap.put(rs.getString("schluessel"), rs.getString("wert"));
        }
        if (aktuellesGeraet != null) aktuellesGeraet.setValues(atributeHashMap);
        DebugLog.addHinweis("GeräteMap erfolgreich geladen");
    }

    public void mapAllSzenarien(final Map<UUID, Geraet> geraetMap, final Map<UUID, Szenario> szenarioMap) throws SQLException {
        final Statement stmt = conn.createStatement();
        DebugLog.addHinweis("Beginne SzenarienMap zu laden");
        final ResultSet rs = stmt.executeQuery("""
                SELECT SZENARIEN.ID, NAME, RYTHMUS, BESCHREIBUNG, AKTION, GERAET, SCHLUESSEL, WERT, POSITION
                FROM SZENARIEN
                JOIN SZENARIEN_INHALT
                ON SZENARIEN.ID = Szenarien_Inhalt.SZENARIO
                ORDER BY SZENARIEN.ID, GERAET
                """);
        //Wert der definitiv nicht in Datenbank vorhanden ist
        UUID lastId = null;
        Szenario aktuellesSzenario = null;
        while (rs.next()) {
            final UUID id = UUID.fromString(rs.getString("id"));
            //Beim ersten Szenario und jedem neuen Gerät Wahr
            if (!id.equals(lastId)) {
                final String name = rs.getString("name");
                aktuellesSzenario = new Szenario(id, name);
                aktuellesSzenario.setBeschreibung(rs.getString("beschreibung"));
                szenarioMap.put(id, aktuellesSzenario);
                lastId = id;
            }
            aktuellesSzenario.getAenderungen().put(rs.getInt("position"), new Szenario.Aenderungen(
                    geraetMap.get(UUID.fromString(rs.getString("geraet"))), rs.getString("schluessel"), rs.getString("wert")
            ));
        }
        DebugLog.addHinweis("SzenarienMap erfolgreich geladen");
    }
}