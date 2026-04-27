package userInterface.views;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

public class View {

    @FXML private Label statusPanel;
    @FXML private TreeView<String> uebersichtTree;
    @FXML private Pane hauptPane;

    @FXML
    public void initialize() {
        uebersichtTree.setShowRoot(false);
        uebersichtTree.setRoot(createTreeModel());
    }

    public Pane getHauptPane() {
        return hauptPane;
    }

    public Label getStatusPanel() {
        return statusPanel;
    }

    public void addUebersichtTreeSelectionListener(ChangeListener<TreeItem<String>> listener) {
        uebersichtTree.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    private TreeItem<String> createTreeModel() {
        TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);

        //TODO Erstellung der Kinderknoten aller drei Kategorien
        root.getChildren().addAll(
            new TreeItem<>("Räume"),
            new TreeItem<>("Geräte"),
            new TreeItem<>("Szenarien")
        );
        return root;
    }



}
