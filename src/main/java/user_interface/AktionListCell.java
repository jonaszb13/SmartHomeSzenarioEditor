package user_interface;

import data.models.fachobjekte.Szenario;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.function.BiConsumer;

public class AktionListCell extends ListCell<Szenario.Aenderung> {

    private final ObservableList<Szenario.Aenderung> liste;
    private final BiConsumer<Integer, Integer> onSwap;
    private final HBox row;
    private final Label infoLabel;
    private final Button aufBtn;
    private final Button abBtn;

    public AktionListCell(final ObservableList<Szenario.Aenderung> liste,
                          final BiConsumer<Integer, Integer> onSwap) {
        this.liste = liste;
        this.onSwap = onSwap;
        infoLabel = new Label();
        HBox.setHgrow(infoLabel, Priority.ALWAYS);
        aufBtn = new Button("↑");
        abBtn = new Button("↓");
        row = new HBox(8, infoLabel, aufBtn, abBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4));
    }

    @Override
    protected void updateItem(final Szenario.Aenderung aenderung, final boolean empty) {
        super.updateItem(aenderung, empty);
        if (empty || aenderung == null) {
            setGraphic(null);
            return;
        }
        final int idx = getIndex();
        final String geraetName = aenderung.geraet() != null ? aenderung.geraet().getName() : "–";
        infoLabel.setText((idx + 1) + ". " + aenderung.beschreibung()
                + "  [" + geraetName + ": " + aenderung.schluessel() + " = " + aenderung.wert() + "]");
        aufBtn.setDisable(idx == 0);
        abBtn.setDisable(idx >= liste.size() - 1);
        aufBtn.setOnAction(e -> onSwap.accept(idx, idx - 1));
        abBtn.setOnAction(e -> onSwap.accept(idx, idx + 1));
        setGraphic(row);
    }
}
