package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataAccess {
    private final String url;
    private final String user;
    private final String password;
    private Connection conn;

    public DataAccess(String url, String user, String password) throws SQLException {
        this.url = url;
        this.user = user;
        this.password = password;
        this.conn = DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        String url = "jdbc:h2:file:./data/mydb;AUTO_SERVER=TRUE";
        String user = "sa";
        String password = "";
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
                    CREATE TABLE IF NOT EXISTS Geraeteart
                    (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL
                    );
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Geraete
                    (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    Art INT REFERENCES Geraeteart(id)
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
                    Geraet INT REFERENCES Geraete(id)
                    );
                """);
    }

}
