package data;

import data.daos.Geraet;
import data.daos.Raum;
import data.daos.Szenario;
import util.DebugLog;
import util.customExceptions.NoGeraetProvidedException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * @throws SQLException Wird geworfen, wenn ungültige werte übergeben werden und Verbindung nicht hergestellt werden kann
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
            List<Class> geraeteKlassen = dataAccess.getGeraeteKlassen("data.daos.geraete");
            dataAccess.getAllData(raumHashMap, geraetHashMap, szenarioHashMap, geraeteKlassen);
        } catch (SQLException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoGeraetProvidedException e) {
            DebugLog.addError(e);
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
        //TODO hat es einen Grund, dass hier executeUpdate verwendet wird
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


    public void getAllData(Map<Integer, Raum> raumMap, Map<Integer, Geraet> geraetMap, Map<Integer, Szenario> szenarioMap, List<Class> geraeteKlasenList) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final Statement stmt = conn.createStatement();
        //Laden der Räume
        ResultSet rs = stmt.executeQuery("SELECT * FROM RAEUME");
        String raumName;
        int raumId;
        while (rs.next()) {
            raumId = rs.getInt("id");
            raumName = rs.getString("name");
            raumMap.put(raumId, new Raum(raumId, raumName));
        }
        //Laden der Geräteklassen
        Map<String, Class> klassenListe = new HashMap<>();
        for (Class aClass : geraeteKlasenList) {
            klassenListe.put(aClass.getName(), aClass);
        }
        //Landen der Geräte
        rs = stmt.executeQuery("""
                SELECT GERAETE.ID, GERAETE.NAME, GERAETE.RAUM, Geraete.ART, SCHLUESSEL, WERT
                FROM Geraete
                JOIN GERAETE_WERTE ON Geraete.ID = GERAETE_WERTE.Geraet
                ORDER BY Geraete.ART, Geraete.ID
                """);
        stmt.close();
        Map<String, String> attributeHashMap = new HashMap<>();
        int letztesGeraetId = -1;
        Geraet aktuellesGeraet = null;
        boolean erstesMal = true;
        String geraeteName;
        int geraeteId;
        String geraeteSchluessel;
        String geraeteWert;
        String geraeteArt;
        int geraeteRaum;
        while (rs.next()) {
            geraeteId = rs.getInt("id");
            geraeteSchluessel = rs.getString("schluessel");
            geraeteWert = rs.getString("wert");
            if (geraeteId == letztesGeraetId) {
                attributeHashMap.put(geraeteSchluessel, geraeteWert);
            } else {
                if (erstesMal) {
                    erstesMal = false;
                }
                else aktuellesGeraet.setValues(attributeHashMap);
                geraeteName = rs.getString("name");
                geraeteArt = rs.getString("art");
                geraeteRaum = rs.getInt("raum");
                aktuellesGeraet = (Geraet) klassenListe.get("data.daos.geraete." + geraeteArt)
                        .getDeclaredConstructor(int.class, String.class, Raum.class)
                        .newInstance(geraeteId, geraeteName, raumMap.get(geraeteRaum));
                attributeHashMap.put(geraeteSchluessel, geraeteWert);
                geraetMap.put(geraeteId, aktuellesGeraet);
                raumMap.get(geraeteRaum).getGeraete().add(aktuellesGeraet);
                letztesGeraetId = geraeteId;
            }
        }
        if (aktuellesGeraet != null) {
            aktuellesGeraet.setValues(attributeHashMap);
        }
        //Laden der Szenarien
        rs = stmt.executeQuery("""
                SELECT SZENARIEN.ID, NAME, RYTHMUS, BESCHREIBUNG, AKTION, GERAET, ATTRIBUT, WERT, POSITION
                FROM SZENARIEN
                JOIN SZENARIEN_INHALT
                ON SZENARIEN.ID = Szenarien_Inhalt.SZENARIO
                ORDER BY SZENARIEN.ID, GERAET
                """);
        letztesGeraetId = -1;
        Szenario aktuellesSzenario = null;

        String szenarioName;
        int szenarioId;
        while (rs.next()) {
           szenarioId = rs.getInt("id");
            if (szenarioId != letztesGeraetId) {
                szenarioName = rs.getString("name");
                aktuellesSzenario = new Szenario(szenarioId, szenarioName);

                aktuellesSzenario.setBeschreibung(rs.getString("beschreibung"));
                szenarioMap.put(szenarioId, aktuellesSzenario);
            }
            //TODO soll das hier liegen oder eher im if-Block und dann der else-Block als Fehlerfall?
            aktuellesSzenario.getAenderungen().put(rs.getInt("position"), new Szenario.Aenderungen(
                    geraetMap.get(rs.getInt("geraet")), rs.getString("schluessel"), rs.getString("wert")
            ));
        }
    }

    /**
     * @param paket Paket in dem die Klassen aller Geräte liegen
     * @return Liste aller Klassen, in dem übergeben Paket
     * @throws NoGeraetProvidedException Wird geworfen, wenn der Ordner leer ist
     */
    private List<Class> getGeraeteKlassen(String paket) throws NoGeraetProvidedException {
        final InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(paket.replaceAll("[.]", "/"));
        if (stream == null) throw new NoGeraetProvidedException("Es wurde keine Geräte Klasse gefunden");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, paket))
                .collect(Collectors.toList());
    }

    /**
     * Subklasse für Fehlerhandling im Lamda-Ausdruck
     * @param className Name der Klasse die gefunden werden soll
     * @param packageName Paket in dem die Klassen aller Geräte liegen
     * @return gefundene Klasse
     */
    private Class getClass(String className, String packageName) {
        Class clazz = null;
        try {
            clazz = Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException eCNF) {
            DebugLog.addError(eCNF);
        }
        return clazz;
    }
}
