package data;

import data.daos.Geraet;
import data.daos.Raum;
import data.daos.Szenario;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
        List<Raum> raumList = new ArrayList<>();
        List<Geraet> GeraetList = new ArrayList<>();
        List<Szenario> SzenarioList = new ArrayList<>();
        try {
            DataAccess dataAccess = new DataAccess(url, user, password);
            dataAccess.setupDatabase();
            dataAccess.getAllData(raumList, GeraetList, SzenarioList);
            List<Class> geraeteKlassen = dataAccess.getGeraeteKlassen();
        } catch (SQLException e) {
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
        //Gerätetypen
        /*
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Lampen (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    gID INT REFERENCES Geraete(id),
                    eingeschaltet boolean
                    )
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Rollladen (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    gID INT REFERENCES Geraete(id),
                    eingeschaltet boolean
                    )
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Heizungen (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    gID INT REFERENCES Geraete(id),
                    eingeschaltet boolean
                    )
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Luefter (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    gID INT REFERENCES Geraete(id),
                    eingeschaltet boolean
                    )
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Sensoren (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    gID INT REFERENCES Geraete(id),
                    eingeschaltet boolean
                    )
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Steckdosen (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    gID INT REFERENCES Geraete(id),
                    eingeschaltet boolean
                    )
                """);
         */

    }

    public void getAllData(List<Raum> raumList, List<Geraet> geraetList, List<Szenario> szenarioList) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM RAEUME");
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            raumList.add(new Raum(id, name));
        }
        rs = stmt.executeQuery("""
                SELECT GERAETE.ID, GERAETE.NAME, GERAETE.RAUM, Geraete.ID, Geraete.NAME, Geraete.ART
                FROM Geraete
                ORDER BY Geraete.ART,Geraete.ID
                """);
        int lastId = -1;
        String lastArt = "";
        Geraet aktuellesGeraet = null;
        while (rs.next()) {
            int id = rs.getInt("id");
            if (!(id == lastId)) {
                String art = rs.getString("art");
                String name = rs.getString("name");
                switch (art) {
                    case "Lampe":
                        //aktuellesGeraet = new Lampe(id, name);
                        break;
                    case " ":
                        //TODO andere Geräte
                        break;
                }
                geraetList.add(aktuellesGeraet);
            }
            lastId = id;
            //String attribut = rs.getString("attribut");
            //Field[] a= aktuellesGeraet;
            System.out.println("");
            //TODO Ausbau Atribute zuweisen
        }
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

    private  List<Class> getGeraeteKlassen() {
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
