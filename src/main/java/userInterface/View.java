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

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class View {
    @FXML
    private TreeView<String> uebersichtTree;
    @FXML
    private Pane hauptPane;
    @FXML
    private VBox statusLogVBox;

    private StatusLogView statusLogView;

    public enum ViewClass {
        RAEUME, GERAETE, SZENARIEN,
        RAUM, GERAET, SZENARIO,
        DEFAULT
    }

    private final DoubleMap<UUID, TreeItem<String>> raumTreeMap = new DoubleMap<>();
    private final DoubleMap<UUID, TreeItem<String>> geraetTreeMap = new DoubleMap<>();
    private final DoubleMap<UUID, TreeItem<String>> szenarioTreeMap = new DoubleMap<>();

    final TreeItem<String> raeume = new TreeItem<>("Räume");
    final TreeItem<String> geraete = new TreeItem<>("Geräte");
    final TreeItem<String> szenarien = new TreeItem<>("Szenarien");

    @FXML
    public void initialize() {
        uebersichtTree.setShowRoot(false);
        final TreeItem<String> root = new TreeItem<>("Root");
        uebersichtTree.setRoot(root);
        root.setExpanded(true);
        statusLogView = new StatusLogView(statusLogVBox);
    }

    public void setHauptPane(final Pane pane) {
        hauptPane.getChildren().setAll(pane);
    }

    public StatusLogView getStatusLogView() {
        return statusLogView;
    }

    public void addUebersichtTreeSelectionListener(ChangeListener<TreeItem<String>> listener) {
        uebersichtTree.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    public void updateTreeModel(final Map<UUID, Raum> raeumeMap, final Map<UUID, Geraet> geraeteMap,
                                final Map<UUID, Szenario> szenarienMap) {
        //TODO Gucke, ob man das mit weniger redundanz hinbekommt

        raumTreeMap.clear();
        geraetTreeMap.clear();
        szenarioTreeMap.clear();

        raeume.getChildren().clear();
        geraete.getChildren().clear();
        szenarien.getChildren().clear();

        raeumeMap.forEach((id, raum) -> {
            TreeItem<String> item = new TreeItem<>(raum.getName());
            raumTreeMap.put(id, item);
            raeume.getChildren().add(item);
        });

        geraeteMap.forEach((id, geraet) -> {
            TreeItem<String> item = new TreeItem<>(geraet.getName());
            geraetTreeMap.put(id, item);
            geraete.getChildren().add(item);
        });

        szenarienMap.forEach((id, szenario) -> {
            TreeItem<String> item = new TreeItem<>(szenario.getName());
            szenarioTreeMap.put(id, item);
            szenarien.getChildren().add(item);
        });

        uebersichtTree.getRoot().getChildren().setAll(List.of(raeume, geraete, szenarien));
    }

    public ViewClass getTreeItemType(final TreeItem<String> item) {
        ViewClass returnValue = ViewClass.DEFAULT;
        if (item == raeume) returnValue = ViewClass.RAEUME;
        else if (item == geraete) returnValue = ViewClass.GERAETE;
        else if (item == szenarien) returnValue = ViewClass.SZENARIEN;
        else if (raumTreeMap.getA(item) != null) returnValue = ViewClass.RAUM;
        else if (geraetTreeMap.getA(item) != null) returnValue = ViewClass.GERAET;
        else if (szenarioTreeMap.getA(item) != null) returnValue = ViewClass.SZENARIO;
        return returnValue;
    }

    public UUID getRaumUuidForItem(final TreeItem<String> item) {
        return raumTreeMap.getA(item);
    }

    public UUID getGeraetUuidForItem(final TreeItem<String> item) {
        return geraetTreeMap.getA(item);
    }

    public UUID getSzenarioUuidForItem(final TreeItem<String> item) {
        return szenarioTreeMap.getA(item);
    }
}
