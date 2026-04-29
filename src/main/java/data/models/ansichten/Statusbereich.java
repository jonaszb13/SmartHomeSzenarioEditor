package data.models.ansichten;

import javafx.scene.Node;
import util.statusmeldungen.StatusLog;
import util.statusmeldungen.Meldung;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class Statusbereich {

    public List<Meldung> getNewMessages(UUID userDataLetzterNode) {
        List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        if (null == userDataLetzterNode) {
            return meldungen;
        }
        //TODO hier vielleicht direkt eine Fehlermeldung schmeißen, falls die id nicht mehr gefunden wird
        int indexNeueMeldung = IntStream.range(0, meldungen.size())
                .filter(i -> meldungen.get(i).getMeldungsId().equals(userDataLetzterNode))
                .findFirst()
                .orElse(-1) + 1;
        if (indexNeueMeldung == 0) {
            StatusLog.addError("Gespeicherte Meldung kann nicht mehr im StatusLog abgerufen werden.");
            return new ArrayList<>();
        }
        return meldungen.subList(indexNeueMeldung, meldungen.size());
    }
}
