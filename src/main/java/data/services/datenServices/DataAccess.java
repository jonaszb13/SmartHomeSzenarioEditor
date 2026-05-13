package data.services.datenServices;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.GeraetFactory;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import util.customExceptions.NoGeraetProvidedException;
import util.statusmeldungen.StatusLog;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Klasse die alle Interaktionen mit der persistenten Datenhaltung handhabt.
 *
 * @author Ben Knirsch
 */
public class DataAccess {
    private final Connection conn;
    private static DataAccess instance;
    private static boolean test;

    /**
     * @param url      Pfad, in dem Datenbank angelegt werden soll
     * @param user     Benutzername des Standartnutzers
     * @param password Passwort des Standartnutzers
     * @throws SQLException Wird geworfen, wenn ungültige Werte übergeben werden
     *                      und Verbindung nicht hergestellt werden kann
     */
    public DataAccess(final String url, final String user, final String password) throws SQLException {
        this.conn = DriverManager.getConnection(url, user, password);
    }

    public static DataAccess getInstance() throws SQLException {
        if (instance == null) {
            try {
                Path dbDir = Path.of(System.getProperty("user.home"), ".smarthomeszenarioeditor");
                Files.createDirectories(dbDir);
                if (test) {
                    instance = new DataAccess("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", "sa", "");
                } else {
                    instance = new DataAccess("jdbc:h2:file:" + dbDir.resolve("mydb") + ";AUTO_SERVER=TRUE", "sa", "");
                }
            } catch (java.io.IOException e) {
                throw new SQLException("Datenbankverzeichnis konnte nicht erstellt werden", e);
            }
        }
        return instance;
    }

    public static void setTest(boolean test) {
        DataAccess.test = test;
    }

    /* package */
    void createTable(final String sql) throws SQLException {
        final Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public CachedRowSet getData(final String sql) throws SQLException {
        CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
        final Statement stmt = conn.createStatement();
        final ResultSet rs = stmt.executeQuery(sql);
        rowSet.populate(rs);
        return rowSet;
    }

    /* package */
    void addRaum(final String sql, final UUID id, final String name) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.setString(2, name);
        pStmt.executeUpdate();
    }

    /* package */
    void updateOneValue(final String sql, final UUID id, final String name) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setString(1, name);
        pStmt.setObject(2, id);
        pStmt.executeUpdate();
    }

    /* package */
    void deleteRaum(final String sql, final UUID id) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.executeUpdate();
    }

    /* package */
    void addGeraet(final String sql, final UUID id, final String name, final String art, final UUID raum) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.setString(2, name);
        pStmt.setString(3, art);
        pStmt.setObject(4, raum);
        pStmt.executeUpdate();
    }

    /* package */
    void addGeraetWert(final String sql, final UUID id, final UUID geraet, final String schluessel, final String wert) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.setObject(2, geraet);
        pStmt.setString(3, schluessel);
        pStmt.setString(4, wert);
        pStmt.executeUpdate();
    }

    /* package */
    void deleteGeraetOrWert(final String sql, final UUID id) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.executeUpdate();
    }

    /* package */
    void updateGeraetName(final String sql, final UUID id, final String name) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setString(1, name);
        pStmt.setObject(2, id);
        pStmt.executeUpdate();
    }

    /* package */
    void updateGeraetRaum(final String sql, final UUID id, final UUID raum) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, raum);
        pStmt.setObject(2, id);
        pStmt.executeUpdate();
    }

    /* package */
    void updateGeratWert(final String sql, final UUID geraet, final String schluessel, final String wert) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setString(1, wert);
        pStmt.setObject(2, geraet);
        pStmt.setString(3, schluessel);
        pStmt.executeUpdate();
    }

    /* package */
    void addSzenario(final String sql, final UUID id, final String name, final String beschreibung, final String status) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.setString(2, name);
        pStmt.setString(3, beschreibung);
        pStmt.setString(4, status);
        pStmt.executeUpdate();
    }

    /* package */
    void putSzenarioInhalte(final String sql, final UUID id, final String beschreibung, final UUID szenario,
                            final UUID geraet, final String schluessel, final String wert,
                            final int position) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.setString(2, beschreibung);
        pStmt.setObject(3, szenario);
        pStmt.setObject(4, geraet);
        pStmt.setString(5, schluessel);
        pStmt.setString(6, wert);
        pStmt.setInt(7, position);
        pStmt.executeUpdate();
    }

    /* package */
    void updateSzenario(final String sql, final String name, final String beschreibung, final UUID id) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setString(1, name);
        pStmt.setString(2, beschreibung);
        pStmt.setObject(3, id);
        pStmt.executeUpdate();
    }

    /* package */
    void deleteSzenarioOrSzenarioInhalt(final String sql, final UUID id) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.executeUpdate();
    }

    /* package */
    void alterSzenarioInhalt(final String sql, final String aktion, final String schluessel, final String wert, final int position, final UUID id) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setString(1, aktion);
        pStmt.setString(2, schluessel);
        pStmt.setString(3, wert);
        pStmt.setInt(4, position);
        pStmt.setObject(5, id);
        pStmt.executeUpdate();
    }

    public void executeTestUpdate(final String sql) throws SQLException {
        final Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
    }
}