package userInterface;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import util.statusmeldungen.Meldung;
import util.statusmeldungen.Meldungstyp;

import java.util.List;
import java.util.UUID;

public class StatusLogView {

    private final VBox vbox;

    public StatusLogView(final VBox vbox) {
        this.vbox = vbox;
    }

    public UUID getLetzteStatusMeldungsId() {
        if (vbox.getChildren().isEmpty()) return null;
        return UUID.fromString(vbox.getChildren().getFirst().getUserData().toString());
    }

    public void addMeldungen(final List<Meldung> meldungen) {
        meldungen.stream()
                .map(this::meldungZuLabel)
                .forEach(vbox.getChildren()::addFirst);
    }

    private Label meldungZuLabel(final Meldung meldung) {
        final Label label = new Label(meldung.getMeldungsTyp() + ": " + meldung.getMeldungstext());
        label.setUserData(meldung.getMeldungsId());
        label.setWrapText(true);
        final String typ = meldung.getMeldungsTyp();
        if (typ.equals(Meldungstyp.FEHLER.getBezeichnung())) {
            label.setStyle("-fx-text-fill: #cc0000");
        } else if (typ.equals(Meldungstyp.METADATEN.getBezeichnung())) {
            label.setStyle("-fx-text-fill: #0000ff");
        } else {
            label.setStyle("-fx-text-fill: #000000");
        }
        return label;
    }
}
