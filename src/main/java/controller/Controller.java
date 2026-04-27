package controller;

import data.models.Model;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import userInterface.views.View;
import util.DebugLog;

import java.io.IOException;
import java.util.Objects;

public class Controller implements ChangeListener<TreeItem<String>> {
    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.view.addUebersichtTreeSelectionListener(this);
        this.model = model;
    }

    @Override
    public void changed(ObservableValue<? extends TreeItem<String>> observable,
                        TreeItem<String> oldValue, TreeItem<String> newValue) {

        //TODO nur für Testen
        DebugLog.addHinweis("Meldung");
        //TODO dieser Aufruf muss in jeden changed (oder einen generischeren)
        view.getStatusPanel().setText(model.getStatusbereich().getNachrichten());

        if (newValue == null) return;

        String fxmlFile = switch (newValue.getValue().strip()) {
            case "Räume"     -> "raum-view.fxml";
            case "Geräte"    -> "geraete-view.fxml";
            case "Szenarien" -> "szenarien-view.fxml";
            default          -> "haupt-view.fxml";
        };

        try {
            Pane neuesPanel = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource("/org/example/ui/" + fxmlFile))
            );
            view.getHauptPane().getChildren().setAll(neuesPanel);
        } catch (IOException e) {
            DebugLog.addError("FXMLLoader konnte nicht geladen werden", e);
        }
    }
}
