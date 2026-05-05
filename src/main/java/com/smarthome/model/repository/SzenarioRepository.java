package com.smarthome.model.repository;

import com.smarthome.model.entity.Geraet;
import com.smarthome.model.entity.Szenario;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class SzenarioRepository {
    private final Database db;

    public SzenarioRepository(final Database db) {
        this.db = db;
    }

    public void mapAllSzenarien(final Map<UUID, Geraet> geraetMap, final Map<UUID, Szenario> szenarioMap) throws SQLException {
        final Statement stmt = db.getConnection().createStatement();
        //language=SQL
        final ResultSet rs = stmt.executeQuery("""
                SELECT SZENARIEN.ID, NAME, RYTHMUS, BESCHREIBUNG, AKTION, GERAET, SCHLUESSEL, WERT, POSITION
                FROM SZENARIEN
                JOIN SZENARIEN_INHALT ON SZENARIEN.ID = Szenarien_Inhalt.SZENARIO
                ORDER BY SZENARIEN.ID, GERAET
                """);
        UUID lastId = null;
        Szenario aktuellesSzenario = null;
        while (rs.next()) {
            final UUID id = UUID.fromString(rs.getString("id"));
            if (!id.equals(lastId)) {
                aktuellesSzenario = new Szenario(id, rs.getString("name"));
                aktuellesSzenario.setBeschreibung(rs.getString("beschreibung"));
                szenarioMap.put(id, aktuellesSzenario);
                lastId = id;
            }
            aktuellesSzenario.getAenderungen().put(rs.getInt("position"), new Szenario.Aenderungen(
                    geraetMap.get(UUID.fromString(rs.getString("geraet"))),
                    rs.getString("schluessel"),
                    rs.getString("wert")
            ));
        }
    }
}
