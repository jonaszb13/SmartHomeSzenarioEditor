package data.models;

import data.models.ansichten.Statusbereich;
import data.models.ansichten.Uebersicht;
import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import data.services.datenServices.DataAccess;
import util.DoubleMap;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//Singleton
public final class Model {


    //TODO Umbau Struktur: Model beinhaltet die drei Grunddatentypen für die allgemeinen Datenhaltung; im Model sind zudem die Ansichten (View-Model), die direkte Objekte beinhalten, die dem Nutzer angezeigt werden sollen (und nur diese Informatione)
    //TODO Das Model wiederum verwendet die Daten, um die VIewModel zu erstellen. Der Controller ruft danach die jeweiligen ViewModel auf, um Informationen in die View zu schieben. Nur das Model direkt macht Datenbankaufrufe (über die drei Grundklassen Geraet, Raum, Szenario)

    //View-Models
    //TODO kann auch gut sein, dass die Viewmodels nicht im Model liegen müssen --> wenn alles Singleton ist, dann müssen die nur auf die getInstance() zugreifen
    private final Uebersicht uebersicht;
    private final Statusbereich statusbereich;


    //Logik-Daten
    //TODO mapping direkt im Konstruktor, damit das wieder final kann
    private Map<UUID, Geraet> geraeteMap;
    private Map<UUID, Raum> raeumeMap;
    private Map<UUID, Szenario> szenarienMap;

    //Singleton-Instanz
    private static Model instance;

    private Model() {
        //TODO Daten in MOdel laden (Logikdaten) --> Anzeigedaten sind für jedes Viewmodel unterschiedlich
        //Zuweisung Logik-Daten
        mappeDatenInModel();

        //Erstellung View-Models
        this.uebersicht = Uebersicht.getInstance();
        this.statusbereich = new Statusbereich();

    }

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    public Map<UUID, Raum> getRaeumeMap() {
        return raeumeMap;
    }

    public Map<UUID, Szenario> getSzenarienMap() {
        return szenarienMap;
    }

    public Map<UUID, Geraet> getGeraeteMap() {
        return geraeteMap;
    }

    public Uebersicht getUebersicht() {
        return Uebersicht.getInstance();
    }

    public Statusbereich getStatusbereich() {
        return statusbereich;
    }

    private void mappeDatenInModel() {
        try {
            geraeteMap = new HashMap<>();
            //TODO das Befüllend er Referenzen sollte nicht über Parameter stattfinden
            DataAccess.getInstance().mapAllGeraete();
        } catch (SQLException sqlE) {
            StatusLog.addError("Fehler beim initialen Laden der Daten", sqlE);
        }
    }

    public void load() throws SQLException {
        DataAccess dataAccess = DataAccess.getInstance();
        geraeteMap = new HashMap<>();
    }

    public record Daten(Map<UUID, Raum> raumMap, Map<UUID, Geraet> geraetMap, Map<UUID, Szenario> szenarioMap) {
    }
}
