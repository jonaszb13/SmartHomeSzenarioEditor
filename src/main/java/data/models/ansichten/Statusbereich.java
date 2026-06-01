package data.models.ansichten;

import util.customExceptions.MessageMissingException;
import util.statusmeldungen.Meldung;
import util.statusmeldungen.StatusLog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class Statusbereich {

    //TODO Linebreak nach ca. 50 Zeichen für zu lange Messages hinzufügen

    public List<Meldung> getNewMessages(final UUID userDataLetzterNode) throws MessageMissingException {
        final List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        if (userDataLetzterNode == null) {
            return meldungen;
        }
        int indexNeueMeldung = IntStream.range(0, meldungen.size())
                .filter(i -> meldungen.get(i).getMeldungsId().equals(userDataLetzterNode))
                .findFirst()
                .orElseThrow(() -> new MessageMissingException("Es liegt eine inkonsistente Datenbasis vor: Eine Meldung konnte nicht im Statuslog gefunden werden.")) + 1;
        if (indexNeueMeldung == 0) {
            StatusLog.addError("Gespeicherte Meldung kann nicht mehr im StatusLog abgerufen werden.");
            return new ArrayList<>();
        }
        return meldungen.subList(indexNeueMeldung, meldungen.size());
    }
}
