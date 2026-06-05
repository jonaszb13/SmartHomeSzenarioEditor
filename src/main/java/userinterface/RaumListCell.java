package userinterface;

import data.models.fachobjekte.DataAccessObject;
import data.models.fachobjekte.Raum;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
    private final Consumer<Raum> onOeffnen;

    private final HBox raumCard;
    private final Label raumNameLabel;
    private final Label geraeteLabel;
    private final CheckBox checkBox;
    private final Button detailButton;

    private ChangeListener<Boolean> checkBoxListener;

    public RaumListCell(final Predicate<UUID> isSelektiert,
                        final BiConsumer<UUID, Boolean> setSelektiert,
                        final Consumer<Raum> onOeffnen) {
        this.isSelektiert = isSelektiert;
        this.setSelektiert = setSelektiert;
        this.onOeffnen = onOeffnen;

        raumNameLabel = new Label();
        raumNameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        geraeteLabel = new Label();
        geraeteLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #555555;");

        final VBox textBox = new VBox(4, raumNameLabel, geraeteLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        checkBox = new CheckBox();
        detailButton = new Button("Einzelansicht öffnen");
        detailButton.setStyle("-fx-font-size: 14;");
        detailButton.setPrefWidth(160);
        detailButton.setPrefHeight(32);

        final VBox controlBox = new VBox(8, checkBox, detailButton);
        controlBox.setAlignment(Pos.TOP_RIGHT);

        raumCard = new HBox(10, textBox, controlBox);
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

        final String geraeteText = raum.getGeraete().isEmpty()
                ? "Zugeordnete Geräte: (keine)"
                : "Zugeordnete Geräte: " + raum.getGeraete().stream()
                .map(DataAccessObject::getName)
                        .collect(Collectors.joining("; "));
        geraeteLabel.setText(geraeteText);

        if (checkBoxListener != null) {
            checkBox.selectedProperty().removeListener(checkBoxListener);
        }
        checkBox.setSelected(isSelektiert.test(raum.getId()));
        checkBoxListener = (obs, alt, neu) -> setSelektiert.accept(raum.getId(), neu);
        checkBox.selectedProperty().addListener(checkBoxListener);

        detailButton.setOnAction(e -> onOeffnen.accept(raum));

        setGraphic(raumCard);
        setPadding(new Insets(4, 0, 4, 0));
    }
}
