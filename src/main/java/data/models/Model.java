package data.models;
import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import data.services.datenServices.DataAccess;
import main.SmartHomeApplication;
import userInterface.View;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//Singleton
public final class Model {
    private final View view;

    //Logik-Daten
    //TODO mapping direkt im Konstruktor, damit das wieder final kann
    private Map<UUID, Geraet> geraeteMap;
    private Map<UUID, Raum> raeumeMap;
    private Map<UUID, Szenario> szenarienMap;

    //Singleton-Instanz
    private static Model instance;

    private Model(View view) {
        //TODO Daten in MOdel laden (Logikdaten) --> Anzeigedaten sind für jedes Viewmodel unterschiedlich
        //Zuweisung Logik-Daten
        this.view = view;
    }

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model(SmartHomeApplication.getView());
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


    public void setData() throws SQLException {
        //TODO Datenzugriff nicht direkt über den Data Access, sondern Zugriff über Grundmodels (Gerät, Raum, Szenario)
        //TODO ggf. Exception schon tiefer abgefangen
        try {
            DataAccess.getInstance();
            geraeteMap = new HashMap<>();
        } catch( SQLException sqlE) {
            StatusLog.addError("Fehler beim initialen Laden der Daten", sqlE);
        }
    }
}
