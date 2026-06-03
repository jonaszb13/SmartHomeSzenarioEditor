package util.statusmeldungen;

import jakarta.inject.Singleton;
import org.apache.commons.lang3.SystemUtils;
import util.FileHandler;
import util.customExceptions.MessageMissingException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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

    //TODO wann soll immer ein Fehlerbericht erstellt werden

    public static void createErrorFile() {
        if (hasError()) {
            boolean fehler = false;
            final String os = System.getProperty("os.name");
            String filePath = "";
            if (os.toLowerCase(Locale.GERMAN).contains("windows")) {
                filePath = System.getenv("LocalAppData") + "\\SmartHomeEditor\\debuglogs";
            } else if (SystemUtils.IS_OS_UNIX) {
                filePath = System.getProperty("user.home") + "/library/SmartHomeEditor/errorlogs";
            } else {
                addError("Betriebssystem konnte nicht ermittlet werden!");
                fehler = true;
            }
            final String filePathWithName = FileHandler.generateFile(filePath, "DebugLog", "txt");
            if (filePathWithName == null) {
                fehler = true;
            } else if (outputErrors(filePathWithName)) {
                fehler = true;
            }
            if (fehler) {
                addError("Es konnte kein Fehlerbericht erstellt werden");
            } else {
                addMetadaten("Es wurde ein Fehlerbericht unter " + filePath + " abgelegt");
            }
        }
        addMetadaten("Das Programm wurde kontrolliert beendet");
    }

    private static boolean outputErrors(final String filePathWithName) {
        boolean fehler = false;
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePathWithName))) {
            final List<Meldung> meldungen = getInstance().getStatusLogEintraege();
            for (final Meldung meldung : meldungen) {
                System.err.println(meldung.getMeldungstext());
                writer.write(meldung.getMeldungsTyp() + ": " + meldung.getMeldungstext() + "\n");
                if (meldung.getStackTrace() != null) {
                    System.err.println(Arrays.toString(meldung.getStackTrace()));
                    writer.write(Arrays.toString(meldung.getStackTrace()));
                }
            }
        } catch (IOException eIO) {
            fehler = true;
        }
        return fehler;
    }

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
