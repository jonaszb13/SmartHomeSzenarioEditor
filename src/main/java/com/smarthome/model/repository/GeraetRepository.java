package com.smarthome.model.repository;

import com.smarthome.model.entity.Geraet;
import com.smarthome.model.entity.Raum;
import com.smarthome.model.service.GeraetFactory;
import com.smarthome.util.StatusLog;
import com.smarthome.util.customExceptions.NoGeraetProvidedException;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GeraetRepository {
    private final Database db;

    public GeraetRepository(final Database db) {
        this.db = db;
    }

    public void mapAllGeraete(final Map<UUID, Raum> raumMap, final Map<UUID, Geraet> geraetMap) throws SQLException, NoGeraetProvidedException {
        final Statement stmt = db.getConnection().createStatement();
        final GeraetFactory gf = GeraetFactory.getInstance();
        //language=SQL
        final ResultSet rs = stmt.executeQuery("""
                SELECT GERAETE.ID, GERAETE.NAME, GERAETE.RAUM, Geraete.ART, SCHLUESSEL, WERT
                FROM Geraete
                JOIN GERAETE_WERTE ON Geraete.ID = GERAETE_WERTE.Geraet
                ORDER BY Geraete.ART, Geraete.ID
                """);
        Map<String, String> atributeHashMap = new HashMap<>();
        UUID lastId = null;
        Geraet aktuellesGeraet = null;
        boolean erstesMal = true;
        while (rs.next()) {
            final UUID id = UUID.fromString(rs.getString("id"));
            if (!id.equals(lastId)) {
                if (erstesMal) erstesMal = false;
                else {
                    aktuellesGeraet.setValues(atributeHashMap);
                    atributeHashMap = new HashMap<>();
                }
                final UUID raumId = UUID.fromString(rs.getString("raum"));
                try {
                    aktuellesGeraet = gf.createGeraet(id, rs.getString("name"),
                            raumMap.get(raumId), rs.getString("art"));
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    StatusLog.addError("Bei der dynamischen Erstellung eines Geräts ist ein Fehler aufgetreten", e);
                }
                atributeHashMap.put(rs.getString("schluessel"), rs.getString("wert"));
                geraetMap.put(id, aktuellesGeraet);
                raumMap.get(raumId).getGeraete().add(aktuellesGeraet);
                lastId = id;
            }
            atributeHashMap.put(rs.getString("schluessel"), rs.getString("wert"));
        }
        if (aktuellesGeraet != null) aktuellesGeraet.setValues(atributeHashMap);
    }
}
