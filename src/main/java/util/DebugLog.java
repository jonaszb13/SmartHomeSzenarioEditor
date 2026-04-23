package util;

import jakarta.inject.Singleton;
import service.FileHandler;
import ui.Message;
import ui.UserInterface;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public final class Errorlog {

    private static Errorlog INSTANCE;

    private List<Meldung> ErrorlogEintraege;

    private Errorlog() {
    }

    public static Errorlog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Errorlog();
        }
        return INSTANCE;
    }

    public List<Meldung> getErrorlogEintraege() {
        return ErrorlogEintraege;
    }

    public static void addError(String error) {
        getInstance().getErrorlogEintraege().add(new Meldung(Meldungstyp.FEHLER, error));
    }

    public static void addError(Exception exception) {
        getInstance().getErrorlogEintraege().add(new Meldung(Meldungstyp.FEHLER, exception.getMessage(), exception));
    }

    public static boolean hasError() {
        return getInstance().getErrorlogEintraege().stream().anyMatch(Meldung::isError);
    }

    public static void endProgramm() {
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
                FileHandler.writeTextFile(errorFile, getInstance().getErrorlogEintraege());
            } catch (IOException eIO) {
                fehler = true;
            }
            if (fehler) {
                addError("Es konnte kein Fehlerbericht erstellt werden");
            } else {
                UserInterface.out(new Message("Es wurde ein Fehlerbericht unter " + filePath + " abgelegt"));
            }
            UserInterface.out(new Message("Das Programm wurde kontrolliert beendet"));
            System.exit(1);
        } else {
            UserInterface.out(new Message("Das Programm wurde kontrolliert beendet"));
            System.exit(0);
        }

    }
}
