package data.services.datenServices;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import jakarta.inject.Singleton;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Singleton
public final class GeraetDataService {

    private static GeraetDataService instance;
    private final DataAccess dataAccess;

    private GeraetDataService(final DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public static GeraetDataService getInstance() throws SQLException {
        if (instance == null) {
            instance = new GeraetDataService(DataAccess.getInstance());
        }
        return instance;
    }

    public boolean addGeraet(final Geraet geraet, final String art, final Map<String, String> attributeMap) {
        boolean erfolgreich = false;
        //language=SQL
        String sql = """
                INSERT INTO GERAETE (id, name, art, raum)
                VALUES (?, ?, ?, ?)
                ;""";
        try {
            dataAccess.addGeraet(sql, geraet.getId(), geraet.getName(), art, geraet.getRaum().getId());
            for (final Map.Entry<String, String> entry : attributeMap.entrySet()) {
                sql = """
                        INSERT INTO GERAETE_WERTE (id, geraet, schluessel, wert)
                        VALUES (?, ?, ?, ?)
                        """;
                dataAccess.addGeraetWert(sql, UUID.randomUUID(), geraet.getId(), entry.getKey(), entry.getValue());
            }
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
            if (!deleteGeraet(geraet)) {
                StatusLog.addError("KATASTROPHALER FEHLER: Falsche Werte in Datenbank");
            }
        }
        return erfolgreich;
    }

    public boolean deleteGeraet(final Geraet geraet) {
        boolean erfolgreich = false;
        //language=SQL
        String sql = """
                DELETE FROM GERAETE_WERTE
                WHERE geraet = ?
                """;
        try {
            dataAccess.deleteGeraetOrWert(sql, geraet.getId());
            sql = """
                    DELETE FROM GERAETE
                    WHERE id = ?
                    """;
            dataAccess.deleteGeraetOrWert(sql, geraet.getId());
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean updateGeraetName(final Geraet geraet, final String newName) {
        boolean erfolgreich = false;
        //language=SQL
        final String sql = """
                UPDATE GERAETE
                SET name = ?
                WHERE id = ?
                """;
        try {
            dataAccess.updateGeraetName(sql, geraet.getId(), newName);
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean updateGeraetRaum(final Geraet geraet, final Raum raum) {
        boolean erfolgreich = false;
        //language=SQL
        final String sql = """
                UPDATE GERAETE
                SET raum = ?
                WHERE id = ?
                """;
        try {
            dataAccess.updateGeraetRaum(sql, geraet.getId(), raum.getId());
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean updateGeraetWert(final Geraet geraet, final String schluessel, final String wert) {
        boolean erfolgreich = false;
        //language=SQL
        final String sql = """
                UPDATE GERAETE_WERTE
                SET wert = ?
                WHERE id = ?
                AND schluessel = ?
                """;
        try {
            dataAccess.updateGeratWert(sql, geraet.getId(), schluessel, wert);
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean updateGeraetWerte(final Geraet geraet, final Map<String, String> attributeMap) {
        boolean erfolgreich = true;
        for (final Map.Entry<String, String> entry : attributeMap.entrySet()) {
            if (!updateGeraetWert(geraet, entry.getKey(), entry.getValue())) {
                erfolgreich = false;
            }
        }
        return erfolgreich;
    }
}
