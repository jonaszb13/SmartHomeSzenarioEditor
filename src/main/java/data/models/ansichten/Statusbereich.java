package data.models.ansichten;

import javafx.scene.Node;
import util.statusmeldungen.StatusLog;
import util.statusmeldungen.Meldung;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Statusbereich {

    //TODO: Umbau auf String anstatt Node
    public List<Meldung> getNewMessages(Node letzterNode) {
        List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        if (null == letzterNode) {
            return meldungen;
        }
        //TODO hier vielleicht direkt eine Fehlermeldung schmeißen, falls die id nicht mehr gefunden wird
        int indexNeueMeldung = IntStream.range(0, meldungen.size())
                .filter(i -> meldungen.get(i).getMeldungsId().equals(letzterNode.getUserData()))
                .findFirst()
                .orElse(-1) + 1;
        if (indexNeueMeldung == 0) {
            StatusLog.addError("Gespeicherte Meldung kann nicht mehr im StatusLog abgerufen werden.");
            return new ArrayList<>();
        }
        return meldungen.subList(indexNeueMeldung, meldungen.size());
    }
}
