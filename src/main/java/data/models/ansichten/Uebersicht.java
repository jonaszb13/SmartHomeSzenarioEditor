package data.models.ansichten;

import javafx.scene.control.TreeItem;
import util.DoubleMap;

import java.util.UUID;

public record Uebersicht(DoubleMap<UUID, TreeItem<String>> raumTreeMap, DoubleMap<UUID, TreeItem<String>> geraetTreeMap,
                         DoubleMap<UUID, TreeItem<String>> szenarioTreeMap) {
}
