package data;

import data.daos.Geraet;
import data.daos.Raum;
import data.daos.Szenario;
import util.Errorlog;
import util.customExceptions.NoGeraetProvidedException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
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
            Errorlog.addError(e);
        }
    }

    /**
     * Methode, die die Datenbank zur persistenten speicherung der Zustände anlegt
     * @throws SQLException wenn Fehler mit er Datenbankverbindung auftritt
     */
    public void setupDatabase() throws SQLException {
        System.out.println("Datenbank verbunden und ggf. neu angelegt.");
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


    public void getAllData(Map<Integer, Raum> raumMap, Map<Integer, Geraet> geraetMap, Map<Integer, Szenario> szenarioMap, List<Class> geraeteKlasenList) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final Statement stmt = conn.createStatement();
        //Laden der Räume
        ResultSet rs = stmt.executeQuery("SELECT * FROM RAEUME");
        while (rs.next()) {
            final int id = rs.getInt("id");
            final String name = rs.getString("name");
            raumMap.put(id, new Raum(id, name));
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
        Map<String, String> atributeHashMap = new HashMap<>();
        int lastId = -1;
        Geraet aktuellesGeraet = null;
        boolean erstesMal = true;
        while (rs.next()) {
            final int id = rs.getInt("id");
            final String schluessel = rs.getString("schluessel");
            final String wert = rs.getString("wert");
            if (id == lastId) {
                atributeHashMap.put(schluessel, wert);
            } else {
                if (erstesMal) erstesMal = false;
                else aktuellesGeraet.setValues(atributeHashMap);
                final String name = rs.getString("name");
                final String art = rs.getString("art");
                final int raum = rs.getInt("raum");
                aktuellesGeraet = (Geraet) klassenListe.get("data.daos.geraete." + art)
                        .getDeclaredConstructor(int.class, String.class, Raum.class)
                        .newInstance(id, name, raumMap.get(raum));
                atributeHashMap.put(schluessel, wert);
                geraetMap.put(id, aktuellesGeraet);
                raumMap.get(raum).getGeraete().add(aktuellesGeraet);
                lastId = id;
            }
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
        lastId = -1;
        Szenario aktuellesSzenario = null;
        while (rs.next()) {
            final int id = rs.getInt("id");
            if (id != lastId) {
                final String name = rs.getString("name");
                aktuellesSzenario = new Szenario(id, name);
                aktuellesSzenario.setBeschreibung(rs.getString("beschreibung"));
                szenarioMap.put(id, aktuellesSzenario);
            }
            aktuellesSzenario.getAenderungen().put(rs.getInt("position"), new Szenario.aenderungen(
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
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
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
            Errorlog.addError(eCNF);
        }
        return clazz;
    }
}
