package data.models.ansichten;

import javafx.scene.control.TreeItem;
import util.DoubleMap;

import java.util.UUID;

public class Uebersicht {
    private final DoubleMap<UUID, TreeItem<String>> raumTreeMap;
    private final DoubleMap<UUID, TreeItem<String>> geraetTreeMap;
    private final DoubleMap<UUID, TreeItem<String>> szenarioTreeMap;

    public Uebersicht(DoubleMap<UUID, TreeItem<String>> raumTreeMap, DoubleMap<UUID, TreeItem<String>> geraetTreeMap, DoubleMap<UUID, TreeItem<String>> szenarioTreeMap) {
        this.raumTreeMap = raumTreeMap;
        this.geraetTreeMap = geraetTreeMap;
        this.szenarioTreeMap = szenarioTreeMap;
    }
}
