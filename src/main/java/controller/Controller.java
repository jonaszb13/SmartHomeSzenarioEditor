package controller;

import userInterface.views.View;
import data.models.Model;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class Controller implements TreeSelectionListener {
    private final Model model;
    private final View view;

    public Controller() {
        this.model = new Model(null, null, null);
        this.view = new View();
        this.view.addUebersichtTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = view.getSelectedNode();
        if (node == null) return;
        //TODO je nach durchgeführter Änderung aufrufen des Models für Abrufen oder Aktualisieren der Daten des Models
        //TODO neue Daten werden in der View angepasst
        view.getHauptPanel().setBackground(Color.PINK);
    }
}
