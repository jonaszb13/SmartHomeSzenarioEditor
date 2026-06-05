package util.statusmeldungen;

import jakarta.inject.Singleton;
import util.customexceptions.MessageMissingException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Singleton
public final class StatusLog {

    private final List<Meldung> statusLogEintraege = new ArrayList<>();

    private StatusLog() {
    }

    private static final class TempLock {
        private static final StatusLog INSTANCE = new StatusLog();
    }

    public static StatusLog getInstance() {
        return TempLock.INSTANCE;
    }

    public List<Meldung> getStatusLogEintraege() {
        return statusLogEintraege;
    }

    public static void clear() {
        getInstance().statusLogEintraege.clear();
    }

    public static void addError(final String error) {
        getInstance().getStatusLogEintraege().add(new Meldung(Meldungstyp.FEHLER, error));
    }

    public static void addError(final Exception exception) {
        getInstance().getStatusLogEintraege().add(new Meldung(Meldungstyp.FEHLER, exception.getMessage(), exception));
    }

    public static void addError(final String error, final Exception exception) {
        getInstance().getStatusLogEintraege().add(new Meldung(Meldungstyp.FEHLER, error, exception));
    }

    public static void addHinweis(final String hinweis) {
        getInstance().getStatusLogEintraege().add(new Meldung(Meldungstyp.HINWEIS, hinweis));
    }

    public static void addMetadaten(final String metadaten) {
        getInstance().getStatusLogEintraege().add(new Meldung(Meldungstyp.METADATEN, metadaten));
    }

    public static boolean hasError() {
        return getInstance().getStatusLogEintraege().stream().anyMatch(Meldung::isError);
    }

    public List<Meldung> getNewMessages(final UUID userDataLetzterNode) throws MessageMissingException {
        final List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        if (userDataLetzterNode == null) {
            return meldungen;
        }
        final int indexNeueMeldung = IntStream.range(0, meldungen.size())
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
