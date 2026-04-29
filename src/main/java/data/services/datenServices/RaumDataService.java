package data.services.datenServices;

import data.models.fachobjekte.Raum;
import jakarta.inject.Singleton;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.UUID;

@Singleton
public final class RaumDataService {

    private static RaumDataService raumDataService;
    private final DataAccess dataAccess;

    private RaumDataService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public static RaumDataService getInstance(DataAccess dataAccess) throws SQLException {
        if (raumDataService == null) {
            raumDataService = new RaumDataService(DataAccess.getInstance());
        }
        return raumDataService;
    }

    public boolean addRaum(String name) {
        boolean erfolgreich = false;
        //language=SQL
        final String sql = """
                INSERT INTO RAEUME ("ID", "NAME")
                VALUES (?, ?)
                """;
        final Raum raum = new Raum(UUID.randomUUID(), name);
        try {
            dataAccess.addRaum(sql, raum.getId(), name);
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean updateRaumName(UUID id, String name) {
        boolean erfolgreich = false;
        //language=SQL
        final String sql = """
                UPDATE RAEUME SET NAME = ?
                WHERE ID = ?;""";
        try {
            dataAccess.updateRaum(sql, id, name);
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean deleteRaum(UUID id) {
        boolean erfolgreich = false;
        //language=SQL
        final String sql = """
                DELETE FROM RAEUME
                WHERE ID = ?;
                """;
        try {
            dataAccess.deleteRaum(sql, id);
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }
}
