package util;

import jakarta.inject.Singleton;
import service.FileHandler;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public final class DebugLog {

    private static DebugLog instance;

    private final List<Meldung> errorlogEintraege = new ArrayList<>();

    private DebugLog() {
    }

    public static DebugLog getInstance() {
        if (instance == null) {
            instance = new DebugLog();
        }
        return instance;
    }

    public List<Meldung> getDebugLogEintraege() {
        return errorlogEintraege;
    }

    public static void addError(String error) {
        getInstance().getDebugLogEintraege().add(new Meldung(Meldungstyp.FEHLER, error));
    }

    public static void addError(Exception exception) {
        getInstance().getDebugLogEintraege().add(new Meldung(Meldungstyp.FEHLER, exception.getMessage(), exception));
    }
    public static void addError(String error, Exception exception) {
        getInstance().getDebugLogEintraege().add(new Meldung(Meldungstyp.FEHLER, error, exception));
    }

    public static void addHinweis(String hinweis) {
        getInstance().getDebugLogEintraege().add(new Meldung(Meldungstyp.HINWEIS, hinweis));
    }

    public static void addMetadaten(String metadaten) {
        getInstance().getDebugLogEintraege().add(new Meldung(Meldungstyp.METADATEN, metadaten));
    }

    public static boolean hasError() {
        return getInstance().getDebugLogEintraege().stream().anyMatch(Meldung::isError);
    }

    public static void createErrorFile() {
        if (hasError()) {
            boolean fehler = false;
            String os = System.getProperty("os.name");
            String filePath = "";
            if (os.toLowerCase().contains("windows")) {
                filePath = System.getenv("LocalAppData") + "\\SmartHomeEditor\\debuglogs";
            } else if (SystemUtils.IS_OS_UNIX) {
                filePath = System.getProperty("user.home") + "/library/SmartHomeEditor/errorlogs";
            } else {
                addError("Betriebssystem konnte nicht ermittlet werden!");
                fehler = true;
            }
            String filePathWithName = FileHandler.generateFile(filePath, "DebugLog", "txt");
            if (filePathWithName == null) {
                fehler = true;
            } else {
                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePathWithName))) {
                    List<Meldung> meldungen = getInstance().getDebugLogEintraege();
                    for (Meldung meldung : meldungen) {
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
