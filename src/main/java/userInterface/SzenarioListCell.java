package userInterface;

import data.models.fachobjekte.Szenario;
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

public class SzenarioListCell extends ListCell<Szenario> {

    private final Predicate<UUID> isSelektiert;
    private final BiConsumer<UUID, Boolean> setSelektiert;
    private final Consumer<Szenario> onOeffnen;
    private final Consumer<Szenario> onAusfuehren;

    private final HBox szenarioCard;
    private final Label szenarioNameLabel;
    private final Label szenarioBeschreibung;
    private final Label anzahlAktionenLabel;
    private final CheckBox checkBox;
    private final Button detailButton;
    private final Button ausfuehrenButton;

    private ChangeListener<Boolean> checkBoxListener;

    public SzenarioListCell(final Predicate<UUID> isSelektiert,
                            final BiConsumer<UUID, Boolean> setSelektiert,
                            final Consumer<Szenario> onOeffnen,
                            final Consumer<Szenario> onAusfuehren) {
        this.isSelektiert = isSelektiert;
        this.setSelektiert = setSelektiert;
        this.onOeffnen = onOeffnen;
        this.onAusfuehren = onAusfuehren;

        szenarioNameLabel = new Label();
        szenarioNameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        szenarioBeschreibung = new Label();
        szenarioBeschreibung.setStyle("-fx-font-size: 14;");

        anzahlAktionenLabel = new Label();
        anzahlAktionenLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #555555;");

        final VBox textBox = new VBox(4, szenarioNameLabel, szenarioBeschreibung, anzahlAktionenLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        checkBox = new CheckBox();
        detailButton = new Button("Einzelansicht öffnen");
        detailButton.setStyle("-fx-font-size: 14;");
        detailButton.setPrefWidth(160);
        detailButton.setPrefHeight(32);

        ausfuehrenButton = new Button("Ausführen");
        ausfuehrenButton.setStyle("-fx-font-size: 14; -fx-background-color: #2e7d32; -fx-text-fill: white;");
        ausfuehrenButton.setPrefWidth(160);
        ausfuehrenButton.setPrefHeight(32);

        final VBox controlBox = new VBox(8, checkBox, detailButton, ausfuehrenButton);
        controlBox.setAlignment(Pos.TOP_RIGHT);

        szenarioCard = new HBox(10, textBox, controlBox);
        szenarioCard.setAlignment(Pos.CENTER_LEFT);
        szenarioCard.setPadding(new Insets(10));
    }

    @Override
    protected void updateItem(final Szenario szenario, final boolean empty) {
        super.updateItem(szenario, empty);

        if (empty || szenario == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        szenarioNameLabel.setText(szenario.getName());
        szenarioBeschreibung.setText(szenario.getBeschreibung());

        final String geraeteText = szenario.getAenderungen().isEmpty()
                ? "Keine Aktionen konfiguriert"
                : "Anzahl der konfigurierten Aktionen: " + szenario.getAenderungen().size();
        anzahlAktionenLabel.setText(geraeteText);

        if (checkBoxListener != null) {
            checkBox.selectedProperty().removeListener(checkBoxListener);
        }
        checkBox.setSelected(isSelektiert.test(szenario.getId()));
        checkBoxListener = (obs, alt, neu) -> setSelektiert.accept(szenario.getId(), neu);
        checkBox.selectedProperty().addListener(checkBoxListener);

        detailButton.setOnAction(e -> onOeffnen.accept(szenario));
        ausfuehrenButton.setOnAction(e -> onAusfuehren.accept(szenario));

        setGraphic(szenarioCard);
        setPadding(new Insets(4, 0, 4, 0));
    }
}
