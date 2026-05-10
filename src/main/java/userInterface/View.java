package userInterface;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;
import data.services.objektServices.GeraetObjektService;
import data.services.objektServices.RaumObjektService;
import data.services.objektServices.SzenarioObjektService;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import util.DoubleMap;
import util.statusmeldungen.StatusLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class View {
    @FXML
    private TreeView<String> uebersichtTree;
    @FXML
    private Pane hauptPane;
    @FXML
    private VBox statusLogVBox;

    @FXML
    public void initialize() {
        uebersichtTree.setShowRoot(false);
        uebersichtTree.setRoot(createTreeModel());
        updateTreeModel(null, null, null);
    }

    public Pane getHauptPane() {
        return hauptPane;
    }

    public VBox getStatusLogVBox() {
        return statusLogVBox;
    }

    public void addUebersichtTreeSelectionListener(ChangeListener<TreeItem<String>> listener) {
        uebersichtTree.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    public TreeView<String> getUebersichtTree() {
        return uebersichtTree;
    }

    private TreeItem<String> createTreeModel() {
        final TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);
        return root;
    }

    //TODO Serviceaufrufe nicht hier, sondern über das Model --> sollte als Parameter übergeben werden
    public void updateTreeModel(Map<UUID, Raum> raumTreeMap, Map<UUID, Geraet> geraetreeMap, Map<UUID, Szenario> szenarioTreeMap) {
        TreeItem<String> root = uebersichtTree.getRoot();
        root.getChildren().clear();
        final TreeItem<String> raeume = new TreeItem<>("Räume");
        final TreeItem<String> geraete = new TreeItem<>("Geräte");
        final TreeItem<String> szenarien = new TreeItem<>("Szenarien");
        root.getChildren().addAll(List.of(raeume, geraete, szenarien));

        try {
            //Räume Einfügen
            List<TreeItem<String>> raumTreeList = new ArrayList<>();
            //TODO diese Aufrufe mit AUrufen zum Wandeln von Datenmaps zu TreeItemsMaps wechseln
            DoubleMap<UUID, TreeItem<String>> map = RaumObjektService.getInstance().getRaumTreeMap();
            for (final Raum r : RaumObjektService.getInstance().getRaumMap().values()) {
                TreeItem<String> item = new TreeItem<>(r.getName());
                raumTreeList.add(item);
                map.put(r.getId(), item);

            }
            raeume.getChildren().addAll(raumTreeList);

            //Geräte Einfügen
            List<TreeItem<String>> geraetTreeList = new ArrayList<>();
            map = GeraetObjektService.getInstance().getGeraetTreeMap();
            for (final Geraet g : GeraetObjektService.getInstance().getGeraetMap().values()) {
                TreeItem<String> item = new TreeItem<>(g.getName());
                geraetTreeList.add(item);
                map.put(g.getId(), item);
            }
            geraete.getChildren().addAll(geraetTreeList);

            //Szenarien einfügen
            List<TreeItem<String>> szenarioTreeList = new ArrayList<>();
            map = SzenarioObjektService.getInstance().getSzenarioTreeMap();
            for (final Szenario sz : SzenarioObjektService.getInstance().getSzenarioMap().values()) {
                final TreeItem<String> item = new TreeItem<>(sz.getName());
                szenarioTreeList.add(item);
                map.put(sz.getId(), item);
            }
            szenarien.getChildren().addAll(szenarioTreeList);

            //TODO detailliertes Exception-Handling
        } catch (Exception e) {
            StatusLog.addError(e);
        }
    }


}
