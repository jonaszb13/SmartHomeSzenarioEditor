package userInterface;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import util.DoubleMap;

import java.util.Map;
import java.util.UUID;

public class View {

    public enum TreeItemType {
        RAEUME_CATEGORY, GERAETE_CATEGORY, SZENARIEN_CATEGORY,
        RAUM, GERAET, SZENARIO,
        UNKNOWN
    }

    @FXML
    private TreeView<String> uebersichtTree;
    @FXML
    private Pane hauptPane;
    @FXML
    private VBox statusLogVBox;

    private final DoubleMap<UUID, TreeItem<String>> raumTreeMap = new DoubleMap<>();
    private final DoubleMap<UUID, TreeItem<String>> geraetTreeMap = new DoubleMap<>();
    private final DoubleMap<UUID, TreeItem<String>> szenarioTreeMap = new DoubleMap<>();

    private TreeItem<String> raeumeKategorie;
    private TreeItem<String> geraeteKategorie;
    private TreeItem<String> szenarienKategorie;

    @FXML
    public void initialize() {
        uebersichtTree.setShowRoot(false);
        uebersichtTree.setRoot(new TreeItem<>("Root"));
        uebersichtTree.getRoot().setExpanded(true);
    }

    public void updateTreeModel(final Map<UUID, Raum> raeume, final Map<UUID, Geraet> geraete,
                                final Map<UUID, Szenario> szenarien) {
        raumTreeMap.clear();
        geraetTreeMap.clear();
        szenarioTreeMap.clear();

        raeumeKategorie = new TreeItem<>("Räume");
        geraeteKategorie = new TreeItem<>("Geräte");
        szenarienKategorie = new TreeItem<>("Szenarien");

        raeume.forEach((id, raum) -> {
            TreeItem<String> item = new TreeItem<>(raum.getName());
            raumTreeMap.put(id, item);
            raeumeKategorie.getChildren().add(item);
        });

        geraete.forEach((id, geraet) -> {
            TreeItem<String> item = new TreeItem<>(geraet.getName());
            geraetTreeMap.put(id, item);
            geraeteKategorie.getChildren().add(item);
        });

        szenarien.forEach((id, szenario) -> {
            TreeItem<String> item = new TreeItem<>(szenario.getName());
            szenarioTreeMap.put(id, item);
            szenarienKategorie.getChildren().add(item);
        });

        uebersichtTree.getRoot().getChildren().setAll(raeumeKategorie, geraeteKategorie, szenarienKategorie);
    }

    public TreeItemType getTreeItemType(final TreeItem<String> item) {
        if (item == raeumeKategorie) return TreeItemType.RAEUME_CATEGORY;
        if (item == geraeteKategorie) return TreeItemType.GERAETE_CATEGORY;
        if (item == szenarienKategorie) return TreeItemType.SZENARIEN_CATEGORY;
        if (raumTreeMap.getA(item) != null) return TreeItemType.RAUM;
        if (geraetTreeMap.getA(item) != null) return TreeItemType.GERAET;
        if (szenarioTreeMap.getA(item) != null) return TreeItemType.SZENARIO;
        return TreeItemType.UNKNOWN;
    }

    public UUID getUuidForItem(final TreeItem<String> item) {
        UUID id = raumTreeMap.getA(item);
        if (id != null) return id;
        id = geraetTreeMap.getA(item);
        if (id != null) return id;
        return szenarioTreeMap.getA(item);
    }

    public void addUebersichtTreeSelectionListener(final ChangeListener<TreeItem<String>> listener) {
        uebersichtTree.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    public Pane getHauptPane() {
        return hauptPane;
    }

    public VBox getStatusLogVBox() {
        return statusLogVBox;
    }
}
