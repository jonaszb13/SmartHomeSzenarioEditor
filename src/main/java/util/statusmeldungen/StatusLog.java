package util.statusmeldungen;

import jakarta.inject.Singleton;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Singleton
public final class StatusLog {

    private static StatusLog instance;

    private final List<Meldung> statusLogEintraege = new ArrayList<>();

    private StatusLog() {
    }

    public static StatusLog getInstance() {
        if (instance == null) {
            instance = new StatusLog();
        }
        return instance;
    }

    public List<Meldung> getStatusLogEintraege() {
        return statusLogEintraege;
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
            } else {
                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePathWithName))) {
                    final List<Meldung> meldungen = getInstance().getStatusLogEintraege();
                    for (final Meldung meldung : meldungen) {
                        System.err.println(meldung.getMeldungstext());
                        writer.write(meldung.getMeldungsTyp() + ": " + meldung.getMeldungstext());
                        if (meldung.getStackTrace() != null) {
                            System.err.println(Arrays.toString(meldung.getStackTrace()));
                            writer.write(Arrays.toString(meldung.getStackTrace()));
                        }
                    }
                } catch (IOException eIO) {
                    fehler = true;
                }
            }
            if (fehler) {
                addError("Es konnte kein Fehlerbericht erstellt werden");
            } else {
                addMetadaten("Es wurde ein Fehlerbericht unter " + filePath + " abgelegt");
            }
        }
        addMetadaten("Das Programm wurde kontrolliert beendet");
    }
}
