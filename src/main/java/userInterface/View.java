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
import java.util.UUID;

public class View {
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

    private TreeItem<String> createTreeModel() {
        TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);

        //TODO Erstellung der Kinderknoten aller drei Kategorien
        TreeItem<String> raeume = new TreeItem<>("Räume");
        TreeItem<String> geraete = new TreeItem<>("Geräte");
        TreeItem<String> szenarien = new TreeItem<>("Szenarien");
        root.getChildren().addAll(
                raeume,
                geraete,
                szenarien
        );

        try {
            //Räume Einfügen
            List<TreeItem<String>> l = new ArrayList<>();
            DoubleMap<UUID, TreeItem<String>> map = RaumObjektService.getInstance().getRaumItemMap();
            for (Raum r : RaumObjektService.getInstance().getRaumMap().values()) {
                TreeItem<String> item = new TreeItem<>(r.getName());
                l.add(item);
                map.put(r.getId(), item);
            }
            raeume.getChildren().addAll(l);

            //Geräte Einfügen
            l = new ArrayList<>();
            map = GeraetObjektService.getInstance().getGeraetTreeMap();
            for (Geraet g : GeraetObjektService.getInstance().getGeraetMap().values()) {
                TreeItem<String> item = new TreeItem<>(g.getName());
                l.add(item);
                map.put(g.getId(), item);
            }
            geraete.getChildren().addAll(l);

            //Szenarien einfügen
            l = new ArrayList<>();
            map = SzenarioObjektService.getInstance().getSzenarioTreeMap();
            for (Szenario sz : SzenarioObjektService.getInstance().getSzenarioMap().values()) {
                TreeItem<String> item = new TreeItem<>(sz.getName());
                l.add(item);
                map.put(sz.getId(), item);
            }
            szenarien.getChildren().addAll(l);

        } catch (Exception e) {
            StatusLog.addError(e);
        }
        return root;
    }


}
