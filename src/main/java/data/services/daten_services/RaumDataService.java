package data.services.daten_services;

import data.models.fachobjekte.Raum;
import jakarta.inject.Singleton;
import util.statusmeldungen.StatusLog;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.UUID;

@Singleton
public final class RaumDataService {

    private static RaumDataService instance;
    private final DataAccess dataAccess;

    private RaumDataService(final DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public static RaumDataService getInstance() throws SQLException {
        if (instance == null) {
            instance = new RaumDataService(DataAccess.getInstance());
        }
        return instance;
    }

    public CachedRowSet getAllRaeume() throws SQLException {
        //language=SQL
        final String sql = "SELECT * FROM RAEUME";
        return dataAccess.getData(sql);
    }

    public boolean addRaum(final Raum raum) {
        boolean erfolgreich = false;
        //language=SQL
        final String sql = """
                INSERT INTO RAEUME (NAME, ID)
                VALUES (?, ?)
                """;
        try {
            dataAccess.updateOneValue(sql, raum.getName(), raum.getId());
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean updateRaumName(final UUID id, final String name) {
        boolean erfolgreich = false;
        //language=SQL
        final String sql = """
                UPDATE RAEUME SET NAME = ?
                WHERE ID = ?;""";
        try {
            dataAccess.updateOneValue(sql, name, id);
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean deleteRaum(final UUID id) {
        boolean erfolgreich = false;
        //language=SQL
        final String sql = """
                DELETE FROM RAEUME
                WHERE ID = ?;
                """;
        try {
            dataAccess.deleteValue(sql, id);
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }
}
