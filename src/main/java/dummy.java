

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class dummy {
    public static void main(String[] args) {
        String url = "jdbc:h2:file:./data/mydb;AUTO_SERVER=TRUE";
        String user = "sa";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Datenbank verbunden und ggf. neu angelegt.");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS Raeume (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL);
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Geraete (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL);
                    """);
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Szenarien (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL);
                    """);
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Szenarien_Inhalt (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL);
                    """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
