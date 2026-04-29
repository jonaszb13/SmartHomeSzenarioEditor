package data.services.dvk;

import util.statusmeldungen.StatusLog;

import java.sql.SQLException;

public class DatabaseCreationService {

    /**
     * Methode, die die Datenbank zur persistenten speicherung der Zustände anlegt
     *
     * @throws SQLException wenn Fehler mit er Datenbankverbindung auftritt
     */
    public static void createDatabase() throws SQLException {
        StatusLog.addHinweis("Datenbank wird verbunden und ggf. neu angelegt");
        DataAccess dataAccess = DataAccess.getInstance();
        dataAccess.createTable("""
                    CREATE TABLE IF NOT EXISTS Raeume (
                    id uuid PRIMARY KEY,
                    name VARCHAR(255) NOT NULL);
                """);
        dataAccess.createTable("""
                    CREATE TABLE IF NOT EXISTS Geraete
                    (
                    id uuid PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    Raum uuid REFERENCES RAEUME(id),
                    Art varchar(63) NOT NULL
                    );
                """);
        dataAccess.createTable("""
                    CREATE TABLE IF NOT EXISTS Szenarien (
                    id uuid PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    Rythmus VARCHAR(255),
                    Status VARCHAR(255),
                    Beschreibung VARCHAR(255)
                    );
                """);
        dataAccess.createTable("""
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
        dataAccess.createTable("""
                    CREATE TABLE IF NOT EXISTS Geraete_Werte (
                    id uuid PRIMARY KEY,
                    Geraet uuid REFERENCES Geraete(id),
                    schluessel VARCHAR(255) NOT NULL,
                    Wert VARCHAR(255)
                    )
                """);
        StatusLog.addHinweis("Datenbank verbunden");
    }
}
