package data.services;

import data.models.Raum;
import jakarta.inject.Singleton;
import util.StatusLog;

import java.sql.SQLException;
import java.util.UUID;

@Singleton
public final class RaumService {

    static RaumService raumService;
    DataAccess dataAccess;

    private RaumService(DataAccess dataAccess) {
    }

    public static RaumService setupRaumService(DataAccess dataAccess) {
        return new RaumService(dataAccess);
    }

    public static RaumService getInstance() {
        return raumService;
    }

    public void addRaum(String name) {
        boolean erfolgreich = false;
        //language=SQL
        String sql = """
                INSERT INTO RAEUME ("ID", "NAME")
                VALUES (?, ?)
                """;
        //Dauerschleife möglich?
        while (!erfolgreich) {
            Raum raum = new Raum(UUID.randomUUID(), name);
            try {
                dataAccess.addRaum(sql, raum.getId(), name);
                erfolgreich = true;
            } catch (SQLException eSQL) {
                StatusLog.addError(eSQL);
            }
        }
    }

    public boolean updateRaum(UUID id, String name) {
        boolean erfolgreich = false;
        //language=SQL
        String sql = """
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
        String sql = """
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
