package util;

import service.FileHandler;
import ui.Message;
import ui.UserInterface;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;


public class Errorlog {
    private static StringBuilder ErrorlogBuilder;

    public static void addError(String error) {
        if (ErrorlogBuilder == null) {
            ErrorlogBuilder = new StringBuilder();
        }
        ErrorlogBuilder.append(LocalDateTime.now()).append(" ").append(error).append("\n");
    }

    public static void addError(Exception e) {
        if (ErrorlogBuilder == null) {
            ErrorlogBuilder = new StringBuilder();
        }
        ErrorlogBuilder.append(LocalDateTime.now()).append(" ")
                .append(e.getMessage()).append(" ")
                .append(Arrays.toString(e.getStackTrace())).append("\n");
    }

    public static boolean hasError() {
        return ErrorlogBuilder != null && !ErrorlogBuilder.isEmpty();
    }

    public static void endProgramm() {
        if (hasError()) {
            boolean fehler = false;
            String errorLog = ErrorlogBuilder.toString();
            String os = System.getProperty("os.name");
            String filePath = "";
            if (os.toLowerCase().contains("windows")) {
                filePath = System.getenv("LocalAppData") + "\\SmartHomeEditor\\errorlogs";
            } else if (SystemUtils.IS_OS_UNIX) {
                filePath = System.getProperty("user.home") + "/library/SmartHomeEditor/errorlogs";
            } else {
                UserInterface.out(new Message("Betriebssystem konnte nicht ermittelt werden!"));
                fehler = true;
            }
            File errorFile = FileHandler.generateFile(filePath, "Errorlog", "txt");
            try {
                FileHandler.writeTextFile(errorFile, errorLog);
            } catch (IOException eIO) {
                fehler = true;
            }
            if (fehler) {
                UserInterface.out(new Message("Es konnte kein Fehlerbericht erstellt werden"));
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
