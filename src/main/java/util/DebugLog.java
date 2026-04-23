package util;

import jakarta.inject.Singleton;
import service.FileHandler;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;

@Singleton
public final class DebugLog {

    private static DebugLog INSTANCE;

    private List<Meldung> ErrorlogEintraege;

    private DebugLog() {
    }

    public static DebugLog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DebugLog();
        }
        return INSTANCE;
    }

    public List<Meldung> getDebugLogEintraege() {
        return ErrorlogEintraege;
    }

    public static void addError(String error) {
        getInstance().getDebugLogEintraege().add(new Meldung(Meldungstyp.FEHLER, error));
    }

    public static void addError(Exception exception) {
        getInstance().getDebugLogEintraege().add(new Meldung(Meldungstyp.FEHLER, exception.getMessage(), exception));
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

    public static void createErrorLog() {
        if (hasError()) {
            boolean fehler = false;
            String os = System.getProperty("os.name");
            String filePath = "";
            if (os.toLowerCase().contains("windows")) {
                filePath = System.getenv("LocalAppData") + "\\SmartHomeEditor\\errorlogs";
            } else if (SystemUtils.IS_OS_UNIX) {
                filePath = System.getProperty("user.home") + "/library/SmartHomeEditor/errorlogs";
            } else {
                addError("Betriebssystem konnte nicht ermittlet werden!");
                fehler = true;
            }
            File errorFile = FileHandler.generateFile(filePath, "Errorlog", "txt");
            try {
                List<Meldung> meldungen = getInstance().getDebugLogEintraege();
                FileWriter fileWriter = new FileWriter(errorFile);
                for (Meldung meldung : meldungen) {
                    PrintWriter pw = new PrintWriter(fileWriter);
                    pw.write(Arrays.toString(meldung.getStackTrace()));
                    FileHandler.writeTextFile(errorFile, meldung.getMeldungstext());
                }
            } catch (IOException eIO) {
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
}
