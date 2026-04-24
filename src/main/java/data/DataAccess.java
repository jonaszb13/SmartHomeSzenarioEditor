package data;

import data.daos.Geraet;
import data.daos.Raum;
import data.daos.Szenario;
import util.DebugLog;
import util.customExceptions.NoGeraetProvidedException;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasse die alle Interaktionen mit der persistenten Datenhaltung handhabt.
 * @author Ben Knirsch
 */
public class DataAccess {
    private final Connection conn;

    /**
     * @param url      Pfad, in dem Datenbank angelegt werden soll
     * @param user     Benutzername des Standartnutzers
     * @param password Passwort des Standartnutzers
     * @throws SQLException Wird geworfen, wenn ungültige Werte übergeben werden und Verbindung nicht hergestellt werden kann
     */
    public DataAccess(String url, String user, String password) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:h2:file:" + url + ";AUTO_SERVER=TRUE", user, password);
    }

    public static void main(String[] args) {
        final String url = "./data/mydb";
        final String user = "sa";
        final String password = "";
        Map<Integer, Raum> raumHashMap = new HashMap<>();
        Map<Integer, Geraet> geraetHashMap = new HashMap<>();
        Map<Integer, Szenario> szenarioHashMap = new HashMap<>();
        try {
            final DataAccess dataAccess = new DataAccess(url, user, password);
            dataAccess.setupDatabase();
            List<Class> geraeteKlassen = GeraetTypHandler.getGeraeteKlassen();
            dataAccess.getAllData(raumHashMap, geraetHashMap, szenarioHashMap);
        } catch (SQLException | NoGeraetProvidedException e) {
            e.printStackTrace();
            DebugLog.addError(e);
            DebugLog.createErrorFile();
        }
    }

    /**
     * Methode, die die Datenbank zur persistenten speicherung der Zustände anlegt
     * @throws SQLException wenn Fehler mit er Datenbankverbindung auftritt
     */
    public void setupDatabase() throws SQLException {
        DebugLog.addHinweis("Datenbank verbunden und ggf. neu angelegt");
        final Statement stmt = conn.createStatement();
        //Allgemeine Tabellen
        stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS Raeume (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL);
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Geraete
                    (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    Raum INT REFERENCES RAEUME(id),
                    Art varchar(63) NOT NULL
                    );
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Szenarien (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    Rythmus VARCHAR(255),
                    Status VARCHAR(255),
                    Beschreibung VARCHAR(255)
                    );
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Szenarien_Inhalt (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    aktion VARCHAR(255) NOT NULL,
                    Szenario INT REFERENCES Szenarien(id),
                    Geraet INT REFERENCES Geraete(id),
                    Attribut VARCHAR(255) NOT NULL,
                    Wert VARCHAR(255),
                    Position INT
                    );
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Geraete_Werte (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    Geraet INT REFERENCES Geraete(id),
                    schluessel VARCHAR(255) NOT NULL,
                    Wert VARCHAR(255)
                    )
                """);
        stmt.close();
    }


    public void getAllData(Map<Integer, Raum> raumMap, Map<Integer, Geraet> geraetMap, Map<Integer, Szenario> szenarioMap) throws SQLException, NoGeraetProvidedException {
        final Statement stmt = conn.createStatement();
        //Laden der Räume
        ResultSet rs = stmt.executeQuery("SELECT * FROM RAEUME");
        while (rs.next()) {
            final int id = rs.getInt("id");
            final String name = rs.getString("name");
            raumMap.put(id, new Raum(id, name));
        }
        //Landen der Geräte
        final GeraetFactory gf = GeraetFactory.getInstance();
        //QUESTION: Sollen geräte ohne Attribute geladen werden?
        rs = stmt.executeQuery("""
                SELECT GERAETE.ID, GERAETE.NAME, GERAETE.RAUM, Geraete.ART, SCHLUESSEL, WERT
                FROM Geraete
                JOIN GERAETE_WERTE ON Geraete.ID = GERAETE_WERTE.Geraet
                ORDER BY Geraete.ART, Geraete.ID
                """);
        Map<String, String> atributeHashMap = new HashMap<>();
        int lastId = -1;
        Geraet aktuellesGeraet = null;
        boolean erstesMal = true;
        while (rs.next()) {
            final int id = rs.getInt("id");
            if (id != lastId) {
                if (erstesMal) erstesMal = false;
                else {
                    aktuellesGeraet.setValues(atributeHashMap);
                    atributeHashMap = new HashMap<>();
                }
                final int raum = rs.getInt("raum");
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
        //Laden der Szenarien
        rs = stmt.executeQuery("""
                SELECT SZENARIEN.ID, NAME, RYTHMUS, BESCHREIBUNG, AKTION, GERAET, ATTRIBUT, WERT, POSITION
                FROM SZENARIEN
                JOIN SZENARIEN_INHALT
                ON SZENARIEN.ID = Szenarien_Inhalt.SZENARIO
                ORDER BY SZENARIEN.ID, GERAET
                """);
        //Wert der definitiv nicht in Datenbank vorhanden ist
        lastId = -1;
        Szenario aktuellesSzenario = null;
        while (rs.next()) {
            final int id = rs.getInt("id");
            //Beim ersten Szenario und jedem neuen Gerät Wahr
            if (id != lastId) {
                final String name = rs.getString("name");
                aktuellesSzenario = new Szenario(id, name);
                aktuellesSzenario.setBeschreibung(rs.getString("beschreibung"));
                szenarioMap.put(id, aktuellesSzenario);
            }
            aktuellesSzenario.getAenderungen().put(rs.getInt("position"), new Szenario.Aenderungen(
                    geraetMap.get(rs.getInt("geraet")), rs.getString("schluessel"), rs.getString("wert")
            ));
        }
    }
}
