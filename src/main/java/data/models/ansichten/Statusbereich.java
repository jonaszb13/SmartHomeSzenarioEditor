package data.models.ansichten;

import util.customExceptions.MessageMissing;
import util.statusmeldungen.StatusLog;
import util.statusmeldungen.Meldung;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class Statusbereich {

    public List<Meldung> getNewMessages(UUID userDataLetzterNode) throws MessageMissing {
        List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        if (null == userDataLetzterNode) {
            return meldungen;
        }
        int indexNeueMeldung = IntStream.range(0, meldungen.size())
                .filter(i -> meldungen.get(i).getMeldungsId().equals(userDataLetzterNode))
                .findFirst()
                .orElseThrow(() -> new MessageMissing("Es liegt eine inkonsistente Datenbasis vor: Eine Meldung konnte nicht im Statuslog gefunden werden.")) + 1;
        if (indexNeueMeldung == 0) {
            StatusLog.addError("Gespeicherte Meldung kann nicht mehr im StatusLog abgerufen werden.");
            return new ArrayList<>();
        }
        return meldungen.subList(indexNeueMeldung, meldungen.size());
    }
}
