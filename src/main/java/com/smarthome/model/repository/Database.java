package com.smarthome.model.repository;

import com.smarthome.util.StatusLog;

import java.sql.*;

public class Database {
    private final Connection conn;

    public Database(final String url, final String user, final String password) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:h2:file:" + url + ";AUTO_SERVER=TRUE", user, password);
    }

    Connection getConnection() {
        return conn;
    }

    public void setupSchema() throws SQLException {
        StatusLog.addHinweis("Datenbank verbunden und ggf. neu angelegt");
        final Statement stmt = conn.createStatement();
        stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Raeume (
                id uuid PRIMARY KEY,
                name VARCHAR(255) NOT NULL);
                """);
        stmt.execute("""
                CREATE TABLE IF NOT EXISTS Geraete (
                id uuid PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                Raum uuid REFERENCES RAEUME(id),
                Art varchar(63) NOT NULL
                );
                """);
        stmt.execute("""
                CREATE TABLE IF NOT EXISTS Szenarien (
                id uuid PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                Rythmus VARCHAR(255),
                Status VARCHAR(255),
                Beschreibung VARCHAR(255)
                );
                """);
        stmt.execute("""
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
        stmt.execute("""
                CREATE TABLE IF NOT EXISTS Geraete_Werte (
                id uuid PRIMARY KEY,
                Geraet uuid REFERENCES Geraete(id),
                schluessel VARCHAR(255) NOT NULL,
                Wert VARCHAR(255)
                )
                """);
        stmt.close();
    }
}
