package data.services.datenServices;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.GeraetFactory;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import data.services.GeraetTypHandler;
import data.services.gvk.GeraetObjektService;
import data.services.gvk.RaumObjektService;
import data.services.gvk.SzenarioObjektService;
import util.customExceptions.NoGeraetProvidedException;
import util.statusmeldungen.StatusLog;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
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

    /**
     * @param url      Pfad, in dem Datenbank angelegt werden soll
     * @param user     Benutzername des Standartnutzers
     * @param password Passwort des Standartnutzers
     * @throws SQLException Wird geworfen, wenn ungültige Werte übergeben werden
     *                      und Verbindung nicht hergestellt werden kann
     */
    public DataAccess(final String url, final String user, final String password) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:h2:file:" + url + ";AUTO_SERVER=TRUE", user, password);
    }

    public static DataAccess getInstance() throws SQLException {
        if (instance == null) {
            instance = new DataAccess("./data/mydb", "sa", "");
        }
        return instance;
    }

    //Nur für @Ben zum Testen
    public static void main(String[] args) {
        final String url = "./data/mydb";
        final String user = "sa";
        final String password = "";
        try {
            final Map<UUID, Raum> raumHashMap = RaumObjektService.getInstance().getRaumMap();
            final Map<UUID, Geraet> geraetHashMap = GeraetObjektService.getInstance().getGeraetMap();
            final Map<UUID, Szenario> szenarioHashMap = SzenarioObjektService.getInstance().getSzenarioMap();
            final DataAccess dataAccess = new DataAccess(url, user, password);
            DatabaseCreationService.createDatabase();
            List<Class<?>> geraeteKlassen = GeraetTypHandler.getGeraeteKlassen();
        } catch (SQLException | NoGeraetProvidedException e) {
            e.printStackTrace();
            StatusLog.addError(e);
            StatusLog.createErrorFile();
        }
        System.out.println("");
    }

    /* package */
    void createTable(String sql) throws SQLException {
        final Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }

    //nicht aufrufen → Map aus RaumObjektService entnehmen
    public void mapAllRaeume(final Map<UUID, Raum> raumMap) throws SQLException {
        final Statement stmt = conn.createStatement();
        //Laden der Räume
        StatusLog.addHinweis("Beginne RäumeMap zu laden");
        final ResultSet rs = stmt.executeQuery("SELECT * FROM RAEUME");
        while (rs.next()) {
            final UUID id = UUID.fromString(rs.getString("id"));
            final String name = rs.getString("name");
            raumMap.put(id, new Raum(id, name));
        }
        StatusLog.addHinweis("RäumeMap erfolgreich geladen");
    }

    //nicht aufrufen → Map aus GeraetObjektService entnehmen
    public void mapAllGeraete(final Map<UUID, Raum> raumMap, final Map<UUID, Geraet> geraetMap) throws SQLException, NoGeraetProvidedException {
        final Statement stmt = conn.createStatement();
        StatusLog.addHinweis("Beginne GeräteMap zu laden");
        final GeraetFactory gf = GeraetFactory.getInstance();
        //TODO QUESTION: Sollen geräte ohne Attribute geladen werden?
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
                final UUID raum = UUID.fromString(rs.getString("raum"));
                try {
                    aktuellesGeraet = gf.createGeraet(id, rs.getString("name"),
                            raumMap.get(raum), rs.getString("art"));
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    StatusLog.addError("Bei der dynamischen Erstellung eines Geräts ist ein Fehler aufgetreten", e);
                    //TODO was mit Null tun?
                }
                atributeHashMap.put(rs.getString("schluessel"), rs.getString("wert"));
                geraetMap.put(id, aktuellesGeraet);
                raumMap.get(raum).getGeraete().add(aktuellesGeraet);
                lastId = id;
            }
            atributeHashMap.put(rs.getString("schluessel"), rs.getString("wert"));
        }
        if (aktuellesGeraet != null) aktuellesGeraet.setValues(atributeHashMap);
        StatusLog.addHinweis("GeräteMap erfolgreich geladen");
    }

    //nicht aufrufen → Map aus SzenarioObjektService entnehmen
    public void mapAllSzenarien(final Map<UUID, Geraet> geraetMap, final Map<UUID, Szenario> szenarioMap) throws SQLException {
        final Statement stmt = conn.createStatement();
        StatusLog.addHinweis("Beginne SzenarienMap zu laden");
        final ResultSet rs = stmt.executeQuery("""
                SELECT SZENARIEN.ID, NAME, RYTHMUS, BESCHREIBUNG, AKTION, GERAET, SCHLUESSEL, WERT, POSITION, SZENARIEN_INHALT.ID AS SIID
                FROM SZENARIEN
                JOIN SZENARIEN_INHALT
                ON SZENARIEN.ID = Szenarien_Inhalt.SZENARIO
                ORDER BY SZENARIEN.ID, GERAET
                """);
        //Wert der definitiv nicht in Datenbank vorhanden ist
        UUID lastId = null;
        Szenario aktuellesSzenario = null;
        while (rs.next()) {
            final UUID id = UUID.fromString(rs.getString("id"));
            //Beim ersten Szenario und jedem neuen Gerät Wahr
            if (!id.equals(lastId)) {
                final String name = rs.getString("name");
                aktuellesSzenario = new Szenario(id, name);
                aktuellesSzenario.setBeschreibung(rs.getString("beschreibung"));
                szenarioMap.put(id, aktuellesSzenario);
                lastId = id;
            }
            aktuellesSzenario.getAenderungen().put(rs.getInt("position"), new Szenario.Aenderung(
                    UUID.fromString(rs.getString("SIID")),
                    geraetMap.get(UUID.fromString(rs.getString("geraet"))),
                    rs.getString("Aktion"),
                    rs.getString("schluessel"),
                    rs.getString("wert")
            ));
        }
        StatusLog.addHinweis("SzenarienMap erfolgreich geladen");
    }

    /* package */
    void addRaum(String sql, UUID id, String name) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.setString(2, name);
        pStmt.executeUpdate();
    }

    /* package */
    void updateRaum(String sql, UUID id, String name) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setString(1, name);
        pStmt.setObject(2, id);
        pStmt.executeUpdate();
    }

    /* package */
    void deleteRaum(String sql, UUID id) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.executeUpdate();
    }

    /* package */
    void addGeraet(String sql, UUID id, String name, String art, UUID raum) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.setString(2, name);
        pStmt.setString(3, art);
        pStmt.setObject(4, raum);
        pStmt.executeUpdate();
    }

    /* package */
    void addGeraetWert(String sql, UUID id, UUID geraet, String schluessel, String wert) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.setObject(2, geraet);
        pStmt.setString(3, schluessel);
        pStmt.setString(4, wert);
        pStmt.executeUpdate();
    }

    /* package */
    void deleteGeraetOrWert(String sql, UUID id) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.executeUpdate();
    }

    /* package */
    void updateGeraetName(String sql, UUID id, String name) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setString(1, name);
        pStmt.setObject(2, id);
        pStmt.executeUpdate();
    }

    /* package */
    void updateGeraetRaum(String sql, UUID id, UUID raum) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, raum);
        pStmt.setObject(2, id);
        pStmt.executeUpdate();
    }

    /* package */
    void updateGeratWert(String sql, UUID geraet, String schluessel, String wert) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setString(1, wert);
        pStmt.setObject(2, geraet);
        pStmt.setString(3, schluessel);
        pStmt.executeUpdate();
    }

    /* package */
    void addSzenario(String sql, UUID id, String name, String beschreibung) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.setString(2, name);
        pStmt.setString(3, beschreibung);
        pStmt.executeUpdate();
    }

    /* package */
    void putSzenarioInhalte(String sql, UUID id, String beschreibung, UUID szenario, UUID geraet, String schluessel, String wert, int position) throws SQLException {
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

    void updateSzenario (String sql, String name, String beschreibung, UUID id) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setString(1, name);
        pStmt.setString(2, beschreibung);
        pStmt.setObject(3, id);
        pStmt.executeUpdate();
    }

    void deleteSzenarioOrSzenarioInhalt(String sql, UUID id) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setObject(1, id);
        pStmt.executeUpdate();
    }

    void alterSzenarioInhalt(String sql, String aktion, String schluessel, String wert, int position, UUID id) throws SQLException {
        final PreparedStatement pStmt = conn.prepareStatement(sql);
        pStmt.setString(1, aktion);
        pStmt.setString(2, schluessel);
        pStmt.setString(3, wert);
        pStmt.setInt(4, position);
        pStmt.setObject(5, id);
        pStmt.executeUpdate();
    }
}