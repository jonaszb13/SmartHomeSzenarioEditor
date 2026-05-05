package com.smarthome.model.repository;

import com.smarthome.model.entity.Raum;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class RaumRepository {
    private final Database db;

    public RaumRepository(final Database db) {
        this.db = db;
    }

    public void mapAllRaeume(final Map<UUID, Raum> raumMap) throws SQLException {
        final Statement stmt = db.getConnection().createStatement();
        final ResultSet rs = stmt.executeQuery("SELECT * FROM RAEUME");
        while (rs.next()) {
            final UUID id = UUID.fromString(rs.getString("id"));
            final String name = rs.getString("name");
            raumMap.put(id, new Raum(id, name));
        }
    }

    public void addRaum(final UUID id, final String name) throws SQLException {
        //language=SQL
        final PreparedStatement pStmt = db.getConnection().prepareStatement("""
                INSERT INTO RAEUME ("ID", "NAME") VALUES (?, ?)
                """);
        pStmt.setObject(1, id);
        pStmt.setString(2, name);
        pStmt.executeUpdate();
    }

    public void updateRaum(final UUID id, final String name) throws SQLException {
        //language=SQL
        final PreparedStatement pStmt = db.getConnection().prepareStatement("""
                UPDATE RAEUME SET NAME = ? WHERE ID = ?
                """);
        pStmt.setString(1, name);
        pStmt.setObject(2, id);
        pStmt.executeUpdate();
    }

    public void deleteRaum(final UUID id) throws SQLException {
        //language=SQL
        final PreparedStatement pStmt = db.getConnection().prepareStatement("""
                DELETE FROM RAEUME WHERE ID = ?
                """);
        pStmt.setObject(1, id);
        pStmt.executeUpdate();
    }
}
