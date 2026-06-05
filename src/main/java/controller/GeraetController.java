package controller;

import data.models.fachobjekte.Geraet;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import userinterface.DynamischeFelderBuilder;

public class GeraetController {

    @FXML private Label geraetNameLabel;
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField typField;
    @FXML private TextField raumField;
    @FXML private VBox attributeContainer;

    private Runnable onBearbeiten;
    private Runnable onAbbrechen;

    public void setGeraet(final Geraet geraet) {
        geraetNameLabel.setText(geraet.getName());
        idField.setText(geraet.getId().toString());
        nameField.setText(geraet.getName());
        typField.setText(geraet.getClass().getSimpleName());
        raumField.setText(geraet.getRaum() != null ? geraet.getRaum().getName() : "–");

        attributeContainer.getChildren().setAll(
                DynamischeFelderBuilder.fuerAnzeige(geraet.getAttributTypen(), geraet.getValues()).getVBox());
    }

    public void setOnBearbeiten(final Runnable onBearbeiten) {
        this.onBearbeiten = onBearbeiten;
    }

    public void setOnAbbrechen(final Runnable onAbbrechen) {
        this.onAbbrechen = onAbbrechen;
    }

    @FXML
    private void handleBearbeiten() {
        if (onBearbeiten != null) onBearbeiten.run();
    }

    @FXML
    private void handleAbbrechen() {
        if (onAbbrechen != null) onAbbrechen.run();
    }
}
