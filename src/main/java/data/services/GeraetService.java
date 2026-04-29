package data.services;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class GeraetService {

    static GeraetService geraetService;
    DataAccess dataAccess;

    private GeraetService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public static GeraetService getInstance() {
        return geraetService;
    }

    public static GeraetService setUpGeraetService(DataAccess dataAccess) {
        return new GeraetService(dataAccess);
    }

    public boolean addGeraet(String name, String art, Raum raum, Map<String, String> attributeMap) {
        boolean erfolgreich = false;
        String sql = """
                INSERT INTO geraet (id, name, art, raum)
                VALUES (?, ?, ?, ?)
                ;""";
        try {
            //TODO Was tun wenn fehler in der Mitte?
            UUID geraetId = UUID.randomUUID();
            dataAccess.addGeraet(sql, geraetId, name, art, raum.getId());
            for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
                sql = """
                        INSERT INTO geraete_werte (id, geraet, schluessel, wert)
                        VALUES (?, ?, ?, ?)
                        """;
                dataAccess.addGeraetWert(sql, UUID.randomUUID(), geraetId, entry.getKey(), entry.getValue());
            }
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean deleteGeraet(Geraet geraet) {
        boolean erfolgreich = false;
        String sql = """
                DELETE FROM geraete_werte
                WHERE geraet = ?
                """;
        try {
            dataAccess.deleteGeraetOrWert(sql, geraet.getId());
            sql = """
                    DELETE FROM geraet
                    WHERE id = ?
                    """;
            dataAccess.deleteGeraetOrWert(sql, geraet.getId());
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean updateGeraetName(Geraet geraet, String newName) {
        boolean erfolgreich = false;
        String sql = """
                UPDATE geraet
                SET name = ?
                WHERE id = ?
                """;
        try {
            dataAccess.updateGeraetName(sql, geraet.getId(), newName);
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean updateGeraetRaum(Geraet geraet, Raum raum) {
        boolean erfolgreich = false;
        String sql = """
                UPDATE geraet
                SET raum = ?
                WHERE id = ?
                """;
        try {
            dataAccess.updateGeraetRaum(sql, geraet.getId(), raum.getId());
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean updateGeraetWert(Geraet geraet, String schluessel, String wert) {
        boolean erfolgreich = false;
        String sql = """
                UPDATE geraete_werte
                SET wert = ?
                WHERE id = ?
                AND schluessel = ?
                """;
        try {
            dataAccess.updateGeratWert(sql, geraet.getId(), schluessel, wert);
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean updateGeraetWerte(Geraet geraet, Map<String, String> attributeMap) {
        boolean erfolgreich = true;
        for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
            if (!updateGeraetWert(geraet, entry.getKey(), entry.getValue())) {
                erfolgreich = false;
            }
        }
        return erfolgreich;
    }
}
