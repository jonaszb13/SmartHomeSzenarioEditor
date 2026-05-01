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
public final class SzenarioDataService {
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
        final String sql = """
                INSERT INTO Szenarien ("ID", "NAME", "BESCHREIBUNG")
                VALUES (?, ?, ?)
                """;
        try {
            dataAccess.addSzenario(sql, szenario.getId(), szenario.getName(), szenario.getBeschreibung());
            erfolgreich = true;
            for (Map.Entry<Integer, Szenario.Aenderung> e : szenario.getAenderungen().entrySet()) {
               if (!addSzenarioInhalt(szenario, e.getValue(), e.getKey())) erfolgreich = false;
            }
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
        boolean erfolgreich = true;
        //language=SQL
        String sql = """
                DELETE FROM SZENARIEN
                WHERE ID = ?
                """;
        try {
            for (Map.Entry<Integer, Szenario.Aenderung> e : szenario.getAenderungen().entrySet()) {
                if (!deleteSzenarioInhalt(e.getValue().id())) erfolgreich = false;
            }
            if (!erfolgreich) {
                dataAccess.deleteSzenarioOrSzenarioInhalt(sql, szenario.getId());
                erfolgreich = true;
            }
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
            dataAccess.putSzenarioInhalte(sql, UUID.randomUUID(), aenderung.beschreibung(), szenario.getId(),
                    aenderung.geraet().getId(), aenderung.schluessel(), aenderung.wert(), position);
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean alterSzenarioInhalt(Szenario.Aenderung aenderung, int position) {
        boolean erfolgreich = false;
        //language=SQL
        String sql = """
                UPDATE SZENARIEN_INHALT
                SET AKTION = ?, SCHLUESSEL = ?, WERT = ?, POSITION = ?
                WHERE ID = ?
                """;
        try {
            dataAccess.alterSzenarioInhalt(sql, aenderung.beschreibung(), aenderung.schluessel(),
                    aenderung.wert(), position, aenderung.id());
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }

    public boolean deleteSzenarioInhalt(UUID id) {
        boolean erfolgreich = false;
        //language=SQL
        String sql = """
                DELETE FROM SZENARIEN_INHALT
                WHERE ID = ?
                """;
        try {
            dataAccess.deleteSzenarioOrSzenarioInhalt(sql, id);
            erfolgreich = true;
        } catch (SQLException eSQL) {
            StatusLog.addError(eSQL);
        }
        return erfolgreich;
    }


}