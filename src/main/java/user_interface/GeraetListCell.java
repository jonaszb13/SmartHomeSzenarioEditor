package user_interface;

import data.models.fachobjekte.Geraet;
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

public class GeraetListCell extends ListCell<Geraet> {

    private final Predicate<UUID> isSelektiert;
    private final BiConsumer<UUID, Boolean> setSelektiert;
    private final Consumer<Geraet> onOeffnen;

    private final HBox geraetCard;
    private final Label nameLabel;
    private final Label typLabel;
    private final Label raumLabel;
    private final CheckBox checkBox;
    private final Button detailButton;

    private ChangeListener<Boolean> checkBoxListener;

    public GeraetListCell(final Predicate<UUID> isSelektiert,
                          final BiConsumer<UUID, Boolean> setSelektiert,
                          final Consumer<Geraet> onOeffnen) {
        this.isSelektiert = isSelektiert;
        this.setSelektiert = setSelektiert;
        this.onOeffnen = onOeffnen;

        nameLabel = new Label();
        nameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        typLabel = new Label();
        typLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #555555;");

        raumLabel = new Label();
        raumLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #555555;");

        final VBox textBox = new VBox(4, nameLabel, typLabel, raumLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        checkBox = new CheckBox();
        detailButton = new Button("Einzelansicht öffnen");
        detailButton.setStyle("-fx-font-size: 14;");
        detailButton.setPrefWidth(160);
        detailButton.setPrefHeight(32);

        final VBox controlBox = new VBox(8, checkBox, detailButton);
        controlBox.setAlignment(Pos.TOP_RIGHT);

        geraetCard = new HBox(10, textBox, controlBox);
        geraetCard.setAlignment(Pos.CENTER_LEFT);
        geraetCard.setPadding(new Insets(10));
    }

    @Override
    protected void updateItem(final Geraet geraet, final boolean empty) {
        super.updateItem(geraet, empty);

        if (empty || geraet == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        nameLabel.setText(geraet.getName());
        typLabel.setText("Typ: " + geraet.getClass().getSimpleName());
        raumLabel.setText("Raum: " + (geraet.getRaum() != null ? geraet.getRaum().getName() : "–"));

        if (checkBoxListener != null) {
            checkBox.selectedProperty().removeListener(checkBoxListener);
        }
        checkBox.setSelected(isSelektiert.test(geraet.getId()));
        checkBoxListener = (obs, alt, neu) -> setSelektiert.accept(geraet.getId(), neu);
        checkBox.selectedProperty().addListener(checkBoxListener);

        detailButton.setOnAction(e -> onOeffnen.accept(geraet));

        setGraphic(geraetCard);
        setPadding(new Insets(4, 0, 4, 0));
    }
}
