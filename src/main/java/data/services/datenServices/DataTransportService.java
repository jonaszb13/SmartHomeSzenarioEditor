package data.services.datenServices;

import util.FileHandler;
import util.statusmeldungen.StatusLog;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

public class DataTransportService {

    private static final String SEMICOLON = ";";

    public boolean exportData(final File file) {
        try {
            return FileHandler.writeTextFile(file, generateAuszug());
        } catch (SQLException e) {
            StatusLog.addError(e);
            return false;
        }
    }

    public boolean importData(final File file) {
        try {
            clearAllData();
            List<String> lines = FileHandler.readTextFile(file);
            int section = 0;
            for (String line : lines) {
                if (line.equals(SEMICOLON)) {
                    section++;
                    continue;
                }
                String sql = buildInsertSql(section, line.split(SEMICOLON, -1));
                if (sql != null) {
                    DataAccess.getInstance().executeUpdate(sql);
                }
            }
            return true;
        } catch (SQLException | FileNotFoundException e) {
            StatusLog.addError(e);
            return false;
        }
    }

    public void clearAllData() throws SQLException {
        DataAccess.getInstance().executeUpdate("DELETE FROM SZENARIEN_INHALT");
        DataAccess.getInstance().executeUpdate("DELETE FROM SZENARIEN");
        DataAccess.getInstance().executeUpdate("DELETE FROM GERAETE_WERTE");
        DataAccess.getInstance().executeUpdate("DELETE FROM GERAETE");
        DataAccess.getInstance().executeUpdate("DELETE FROM RAEUME");
    }

    private String buildInsertSql(final int section, final String[] col) {
        return switch (section) {
            case 0 -> String.format(
                    "INSERT INTO RAEUME (ID, NAME) VALUES ('%s', '%s')",
                    esc(col[0]), esc(col[1]));
            case 1 -> String.format(
                    "INSERT INTO GERAETE (ID, NAME, RAUM, ART) VALUES ('%s', '%s', '%s', '%s')",
                    esc(col[0]), esc(col[1]), esc(col[2]), esc(col[3]));
            case 2 -> String.format(
                    "INSERT INTO GERAETE_WERTE (ID, GERAET, SCHLUESSEL, WERT) VALUES ('%s', '%s', '%s', '%s')",
                    esc(col[0]), esc(col[1]), esc(col[2]), esc(col[3]));
            case 3 -> String.format(
                    "INSERT INTO SZENARIEN (ID, NAME, BESCHREIBUNG) VALUES ('%s', '%s', '%s')",
                    esc(col[0]), esc(col[1]), esc(col[2]));
            case 4 -> String.format(
                    "INSERT INTO SZENARIEN_INHALT (ID, AKTION, SZENARIO, GERAET, SCHLUESSEL, WERT, POSITION) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', %s)",
                    esc(col[0]), esc(col[1]), esc(col[2]), esc(col[3]), esc(col[4]), esc(col[5]), col[6]);
            default -> null;
        };
    }

    private static String esc(final String value) {
        return value == null ? "" : value.replace("'", "''");
    }

    private String generateAuszug() throws SQLException {
        return generateRaumauszug() + generateGeraetAuszug() + generateSzenarioAuszug();
    }

    private String generateRaumauszug() throws SQLException {
        StringBuilder sb = new StringBuilder();
        CachedRowSet crs = DataAccess.getInstance().getData("SELECT ID, NAME FROM RAEUME");
        while (crs.next()) {
            sb.append(crs.getObject(1)).append(SEMICOLON).append(crs.getString(2)).append("\n");
        }
        return sb.append(";\n").toString();
    }

    private String generateGeraetAuszug() throws SQLException {
        StringBuilder sb = new StringBuilder();
        CachedRowSet crs = DataAccess.getInstance().getData("SELECT ID, NAME, RAUM, ART FROM GERAETE");
        while (crs.next()) {
            sb.append(crs.getObject(1)).append(SEMICOLON).append(crs.getString(2))
                    .append(SEMICOLON).append(crs.getObject(3)).append(SEMICOLON).append(crs.getString(4)).append("\n");
        }
        sb.append(";\n");
        crs = DataAccess.getInstance().getData("SELECT ID, GERAET, SCHLUESSEL, WERT FROM GERAETE_WERTE");
        while (crs.next()) {
            sb.append(crs.getObject(1)).append(SEMICOLON).append(crs.getObject(2))
                    .append(SEMICOLON).append(crs.getString(3)).append(SEMICOLON).append(crs.getString(4)).append("\n");
        }
        return sb.append(";\n").toString();
    }

    private String generateSzenarioAuszug() throws SQLException {
        StringBuilder builder = new StringBuilder();
        CachedRowSet crs = DataAccess.getInstance().getData("SELECT ID, NAME, BESCHREIBUNG FROM SZENARIEN");
        while (crs.next()) {
            builder.append(crs.getObject(1)).append(SEMICOLON).append(crs.getString(2))
                    .append(SEMICOLON).append(crs.getString(3)).append("\n");
        }
        builder.append(";\n");
        crs = DataAccess.getInstance().getData("SELECT ID, AKTION, SZENARIO, GERAET, SCHLUESSEL, WERT, POSITION FROM SZENARIEN_INHALT");
        while (crs.next()) {
            builder.append(crs.getObject(1)).append(SEMICOLON).append(crs.getString(2))
                    .append(SEMICOLON).append(crs.getObject(3)).append(SEMICOLON).append(crs.getObject(4))
                    .append(SEMICOLON).append(crs.getString(5)).append(SEMICOLON).append(crs.getString(6))
                    .append(SEMICOLON).append(crs.getInt(7)).append("\n");
        }
        return builder.append(";\n").toString();
    }
}
