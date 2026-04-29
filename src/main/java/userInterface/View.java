package userInterface;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class View {
    @FXML private TreeView<String> uebersichtTree;
    @FXML private Pane hauptPane;
    @FXML private VBox statusLogVBox;

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
        root.getChildren().addAll(
            new TreeItem<>("Räume"),
            new TreeItem<>("Geräte"),
            new TreeItem<>("Szenarien")
        );
        return root;
    }



}
