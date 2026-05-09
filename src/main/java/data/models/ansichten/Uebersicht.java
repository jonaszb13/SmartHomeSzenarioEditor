
package data.models.ansichten;

import data.models.Model;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//Klasse soll nur die Daten bereitstellen --> im Controller soll

//Singleton
public class Uebersicht {

    //nicht final, da nur GUI-Elemente
    private Map<UUID, TreeItem<String>> raumTreeMap;
    private Map<UUID, TreeItem<String>> geraetTreeMap;
    private Map<UUID, TreeItem<String>> szenarioTreeMap;

    private static Uebersicht instance;

    private Uebersicht(){
       // raumTreeMap = mappeRaumTree();
       // geraetTreeMap = mappeGeraetTree();
       // szenarioTreeMap = mappeSzenarioTree();
    }

    public static Uebersicht getInstance() {
        if (instance == null) {
            instance = new Uebersicht();
        }
        return instance;
    }

    public Map<UUID, TreeItem<String>> getRaumTreeMap() {
        return raumTreeMap;
    }
    /*
    private Map<UUID, TreeItem<String>> mappeRaumTree() {
        Model.getInstance().getRaeumeMap().values();
    }

    private Map<UUID, TreeItem<String>> mappeGeraetTree() {

    }

    private Map<UUID, TreeItem<String>> mappeSzenarioTree() {

    }

     */
}


