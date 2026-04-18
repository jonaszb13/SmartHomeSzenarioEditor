package data;

import data.daos.Geraet;
import data.daos.Raum;
import data.daos.Szenario;
import util.Errorlog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataAccess {
    private final Connection conn;

    public DataAccess(String url, String user, String password) throws SQLException {
        this.conn = DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        String url = "jdbc:h2:file:./data/mydb;AUTO_SERVER=TRUE";
        String user = "sa";
        String password = "";
        Map<Integer, Raum> raumList = new HashMap<>();
        Map<Integer, Geraet> GeraetList = new HashMap<>();
        Map<Integer, Szenario> SzenarioList = new HashMap<>();
        try {
            DataAccess dataAccess = new DataAccess(url, user, password);
            dataAccess.setupDatabase();
            List<Class> geraeteKlassen = dataAccess.getGeraeteKlassen();
            dataAccess.getAllData(raumList, GeraetList, SzenarioList, geraeteKlassen);
        } catch (SQLException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupDatabase() throws SQLException {
        System.out.println("Datenbank verbunden und ggf. neu angelegt.");
        Statement stmt = conn.createStatement();
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
                    Wert VARCHAR(255)
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
    }

    public void getAllData(Map<Integer, Raum> raumList, Map<Integer, Geraet> geraetList, Map<Integer, Szenario> szenarioList, List<Class> geraeteKlasen) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Statement stmt = conn.createStatement();
        //Laden der Räume
        ResultSet rs = stmt.executeQuery("SELECT * FROM RAEUME");
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            raumList.put(id, new Raum(id, name));
        }
        //Laden der Geräteklassen
        HashMap<String, Class> klassenListe = new HashMap<>();
        for (Class aClass : geraeteKlasen) {
            klassenListe.put(aClass.getName(), aClass);
        }
        //Landen der Geräte
        rs = stmt.executeQuery("""
                SELECT GERAETE.ID, GERAETE.NAME, GERAETE.RAUM, Geraete.ART, SCHLUESSEL, WERT
                FROM Geraete
                JOIN GERAETE_WERTE ON Geraete.ID = GERAETE_WERTE.Geraet
                ORDER BY Geraete.ART, Geraete.ID
                """);
        HashMap<String, String> atributeHashMap = new HashMap<>();
        int lastId = -1;
        Geraet aktuellesGeraet = null;
        boolean erstesMal = true;
        while (rs.next()) {
            int id = rs.getInt("id");
            String schluessel = rs.getString("schluessel");
            String wert = rs.getString("wert");
            if (id != lastId) {
                if (erstesMal) erstesMal = false;
                else aktuellesGeraet.setValues(atributeHashMap);
                String name = rs.getString("name");
                String art = rs.getString("art");
                int raum = rs.getInt("raum");
                try {
                    aktuellesGeraet = (Geraet) klassenListe.get("data.daos.geraete." + art)
                            .getDeclaredConstructor(int.class, String.class, Raum.class)
                            .newInstance(id, name, raumList.get(raum));
                    atributeHashMap.put(schluessel, wert);
                    geraetList.put(id, aktuellesGeraet);
                    raumList.get(raum).getGeraete().add(aktuellesGeraet);
                    lastId = id;
                } catch (Exception e) {
                    Errorlog.addError(e);
                }
            } else {
                atributeHashMap.put(schluessel, wert);
            }
        }
        if (aktuellesGeraet != null) aktuellesGeraet.setValues(atributeHashMap);
        //Laden der Szenarien
        rs = stmt.executeQuery("""
                SELECT SZENARIEN.ID, NAME, RYTHMUS, BESCHREIBUNG, AKTION, GERAET, ATTRIBUT, WERT
                FROM SZENARIEN
                JOIN SZENARIEN_INHALT
                ON SZENARIEN.ID = Szenarien_Inhalt.SZENARIO
                ORDER BY SZENARIEN.ID, GERAET
                """);
        lastId = -1;
        Szenario aktuellesSzenario = null;
        while (rs.next()) {
            int id = rs.getInt("id");
            if (id != lastId) {
                String name = rs.getString("name");
                aktuellesSzenario = new Szenario(id, name);
                aktuellesSzenario.setBeschreibung(rs.getString("beschreibung"));
                szenarioList.put(id, aktuellesSzenario);
            }
            aktuellesSzenario.getAenderungen().add(new Szenario.aenderungen(
                    geraetList.get(rs.getInt("geraet")), rs.getString("schluessel"), rs.getString("wert")
            ));
        }
    }

    private List<Class> getGeraeteKlassen() throws Exception {
        String paket = "data.daos.geraete";
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(paket.replaceAll("[.]", "/"));
        if (stream == null) throw new Exception();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, paket))
                .collect(Collectors.toList());
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException eCNF) {
            Errorlog.addError(eCNF);
            return null;
        }
    }
}
