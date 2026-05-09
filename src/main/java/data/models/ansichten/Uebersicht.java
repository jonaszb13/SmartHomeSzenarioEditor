
package data.models.ansichten;

import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//Klasse soll nur die Daten bereitstellen --> im Controller soll
public class Uebersicht {

    private final Map<UUID, TreeItem<String>> raumTreeMap;
    private final Map<UUID, TreeItem<String>> geraetTreeMap;
    private final Map<UUID, TreeItem<String>> szenarioTreeMap;

    private static Uebersicht instance;

    private Uebersicht(){
        this.raumTreeMap = new HashMap<>();
        this.geraetTreeMap = new HashMap<>();
        this.szenarioTreeMap = new HashMap<>();
    }

    public static Uebersicht getInstance() {
        if (instance == null) {
            instance = new Uebersicht();
        }
        return instance;
    }

    private void mappeDatenFuerGui() {

    }
}


