package data.services.datenServices;

import data.models.fachobjekte.Szenario;
import jakarta.inject.Singleton;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

/**
 * Klasse die übergebene Objekte in SQL Statements für die persistente datenhaltung umwandelt
 */
@Singleton
public class SzenarioDataService {
    private static SzenarioDataService instance;
    private final DataAccess dataAccess;

    private SzenarioDataService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public static SzenarioDataService getInstance(DataAccess dataAccess) {
        if (instance == null) {
            instance = new SzenarioDataService(dataAccess);
        }
        return instance;
    }

    public boolean addSzenario(Szenario szenario) {
        boolean erfolgreich = false;
        //language=SQL
        String sql = """
                INSERT INTO Szenarien ("ID", "NAME", "BESCHREIBUNG")
                VALUES (?, ?, ?)
                """;
        try {
            dataAccess.addSzenario(sql, szenario.getId(), szenario.getName(), szenario.getBeschreibung());
            for (Map.Entry<Integer, Szenario.Aenderung> e : szenario.getAenderungen().entrySet()) {
                sql = """
                        INSERT INTO SZENARIEN_INHALT ("ID", "AKTION", "SZENARIO", "GERAET", "SCHLUESSEL", "WERT", "POSITION") 
                        VALUES (?, ?, ?, ?, ?, ?)
                        """;
                dataAccess.putSzenarioInhalte(sql, UUID.randomUUID(), e.getValue().aktion(), szenario.getId(),
                        e.getValue().geraet().getId(), e.getValue().attribut(), e.getValue().value(), e.getKey());
            }
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean updateSzenario(Szenario szenario) {
        boolean erfolgreich = false;
        //language=SQL
        String sql = """
                UPDATE SZENARIEN
                SET NAME = ?, BESCHREIBUNG = ?
                WHERE ID = ?
                """;
        try {
            dataAccess.updateSzenario(sql, szenario.getName(), szenario.getBeschreibung(), szenario.getId());
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean deleteSzenario(Szenario szenario) {
        boolean erfolgreich = false;
        //language=SQL
        String sql = """
                DELETE FROM SZENARIEN
                WHERE ID = ?
                """;
        try {
            dataAccess.deleteSzenario(sql, szenario.getId());
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean addSzenarioInhalt(Szenario szenario, Szenario.Aenderung aenderung, int position) {
        boolean erfolgreich = false;
        //language=SQL
        String sql = """
                INSERT INTO SZENARIEN_INHALT ("ID", "AKTION", "SZENARIO", "GERAET", "SCHLUESSEL", "WERT", "POSITION") 
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try {
            dataAccess.putSzenarioInhalte(sql, UUID.randomUUID(), aenderung.aktion(), szenario.getId(), aenderung.geraet().getId(), aenderung.attribut(), aenderung.value(), position);

        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean alterSzenarioInhalt(Szenario szenario, Szenario.Aenderung aenderung, int position) {
        boolean erfolgreich = false;
        //language=SQL
        String sql = """
                INSERT INTO SZENARIEN_INHALT ("ID", "AKTION", "SZENARIO", "GERAET", "SCHLUESSEL", "WERT", "POSITION") 
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try {
            dataAccess.putSzenarioInhalte(sql, UUID.randomUUID(), aenderung.aktion(), szenario.getId(), aenderung.geraet().getId(), aenderung.attribut(), aenderung.value(), position);
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean deleteSzenarioInhalt(Szenario szenario, int position) {
        boolean erfolgreich = false;
        //language=SQL
        String sql = """
                DELETE FROM SZENARIEN_INHALT 
                WHERE SZENARIO = ?
                AND POSITION = ?
                """;
        try {
            dataAccess.deleteSzenarioInhalt(sql, szenario.getId(), position);
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }


}
