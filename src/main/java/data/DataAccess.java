package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setupDatabase() throws SQLException {
        System.out.println("Datenbank verbunden und ggf. neu angelegt.");
        Statement stmt = conn.createStatement();
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
                    Art VARCHAR(255),
                    Attribut VARCHAR(255),
                    Wert VARCHAR(255)
                    );
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Szenarien (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    Rythmus VARCHAR(255) NOT NULL,
                    Status VARCHAR(255) NOT NULL
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
        while (rs.next()) {
            int id = rs.getInt("id");
            if (id == lastId) {
                //TODO Mapping gleiches Objekt
                break;
            }
            String art = rs.getString("art");
            if (!lastArt.equals(art)) {
                //TODO Mapping andere Geräteart
            }
            //TODO neues Gerät anlegen
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

}
