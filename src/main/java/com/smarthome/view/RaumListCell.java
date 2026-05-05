package com.smarthome.view;

import com.smarthome.model.entity.DAO;
import com.smarthome.model.entity.Raum;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RaumListCell extends ListCell<Raum> {

    private final Predicate<UUID> isSelektiert;
    private final BiConsumer<UUID, Boolean> setSelektiert;
    private final Consumer<Raum> onRaumDetailOeffnen;
    private final Consumer<Raum> onRaumBearbeiten;

    private final HBox raumCard;
    private final Label raumNameLabel;
    private final Label raumGeraeteLabel;
    private final CheckBox raumCheckBox;
    private final Button raumDetailButton;
    private final Button raumBearbeitenButton;

    private ChangeListener<Boolean> checkBoxListener;

    public RaumListCell(final Predicate<UUID> isSelektiert,
                        final BiConsumer<UUID, Boolean> setSelektiert,
                        final Consumer<Raum> onRaumDetailOeffnen,
                        final Consumer<Raum> onRaumBearbeiten) {
        this.isSelektiert = isSelektiert;
        this.setSelektiert = setSelektiert;
        this.onRaumDetailOeffnen = onRaumDetailOeffnen;
        this.onRaumBearbeiten = onRaumBearbeiten;

        raumNameLabel = new Label();
        raumNameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: black;");

        raumGeraeteLabel = new Label();
        raumGeraeteLabel.setStyle("-fx-font-size: 12; -fx-text-fill: black;");

        VBox leftBox = new VBox(4, raumNameLabel, raumGeraeteLabel);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(leftBox, Priority.ALWAYS);

        raumCheckBox = new CheckBox();

        raumDetailButton = new Button("Öffnen");
        raumBearbeitenButton = new Button("Bearbeiten");

        HBox raumButtonGroup = new HBox(5, raumDetailButton, raumBearbeitenButton);
        raumButtonGroup.setAlignment(Pos.CENTER_RIGHT);

        VBox rightBox = new VBox(8, raumCheckBox, raumButtonGroup);
        rightBox.setAlignment(Pos.TOP_RIGHT);

        raumCard = new HBox(10, leftBox, rightBox);
        raumCard.setAlignment(Pos.CENTER_LEFT);
        raumCard.setPadding(new Insets(10));
    }

    @Override
    protected void updateItem(final Raum raum, final boolean empty) {
        super.updateItem(raum, empty);

        if (empty || raum == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        raumNameLabel.setText(raum.getName());

        String geraeteText = raum.getGeraete().isEmpty()
                ? "Zugeordnete Geräte: (keine)"
                : "Zugeordnete Geräte: " + raum.getGeraete().stream()
                        .map(DAO::getName)
                        .collect(Collectors.joining("; "));
        raumGeraeteLabel.setText(geraeteText);

        if (checkBoxListener != null) {
            raumCheckBox.selectedProperty().removeListener(checkBoxListener);
        }
        raumCheckBox.setSelected(isSelektiert.test(raum.getId()));
        checkBoxListener = (obs, alt, neu) -> setSelektiert.accept(raum.getId(), neu);
        raumCheckBox.selectedProperty().addListener(checkBoxListener);

        raumDetailButton.setOnAction(e -> onRaumDetailOeffnen.accept(raum));
        raumBearbeitenButton.setOnAction(e -> onRaumBearbeiten.accept(raum));

        setGraphic(raumCard);
        setPadding(new Insets(4, 0, 4, 0));
    }
}
