package data;

import data.daos.Geraet;
import data.daos.Raum;
import data.daos.Szenario;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        HashMap<Integer, Raum> raumList = new HashMap<>();
        List<Geraet> GeraetList = new ArrayList<>();
        List<Szenario> SzenarioList = new ArrayList<>();
        try {
            DataAccess dataAccess = new DataAccess(url, user, password);
            dataAccess.setupDatabase();
            List<Class> geraeteKlassen = dataAccess.getGeraeteKlassen();
            dataAccess.getAllData(raumList, GeraetList, SzenarioList, geraeteKlassen);
        } catch (SQLException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
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

    public void getAllData(HashMap<Integer, Raum> raumList, List<Geraet> geraetList, List<Szenario> szenarioList, List<Class> geraeteKlasen) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
                aktuellesGeraet = (Geraet) klassenListe.get("data.daos.geraete." + art)
                        .getDeclaredConstructor(int.class, String.class, Raum.class)
                        .newInstance(id, name, raumList.get(raum));
                atributeHashMap.put(schluessel, wert);
                geraetList.add(aktuellesGeraet);
                raumList.get(raum).getGeraete().add(aktuellesGeraet);
                lastId = id;
            } else {
                atributeHashMap.put(schluessel, wert);
            }
        }
        if (aktuellesGeraet != null) aktuellesGeraet.setValues(atributeHashMap);
        //Laden der Szenarien
        rs = stmt.executeQuery("""
                SELECT *
                FROM SZENARIEN
                JOIN SZENARIEN_INHALT
                ON SZENARIEN.ID = Szenarien_Inhalt.SZENARIO
                ORDER BY SZENARIEN.ID
                """);
        while (rs.next()) {

        }
    }

    private List<Class> getGeraeteKlassen() {
        String paket = "data.daos.geraete";
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(paket.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, paket))
                .collect(Collectors.toList());
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
            //TODO Logging
            e.printStackTrace();
        }
        return null;
    }
}
