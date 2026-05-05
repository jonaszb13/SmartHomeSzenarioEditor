package com.smarthome.util;

import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class StatusLog {

    public interface MeldungsListener {
        void onNeueMeldung(Meldung meldung);
    }

    private static StatusLog instance;

    private final List<Meldung> statusLogEintraege = new ArrayList<>();
    private final List<MeldungsListener> listeners = new ArrayList<>();

    private StatusLog() {
    }

    public static void addListener(final MeldungsListener listener) {
        getInstance().listeners.add(listener);
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

    private void addMeldung(final Meldung meldung) {
        statusLogEintraege.add(meldung);
        listeners.forEach(l -> l.onNeueMeldung(meldung));
    }

    public static void addError(final String error) {
        getInstance().addMeldung(new Meldung(Meldungstyp.FEHLER, error));
    }

    public static void addError(final Exception exception) {
        getInstance().addMeldung(new Meldung(Meldungstyp.FEHLER, exception.getMessage(), exception));
    }

    public static void addError(final String error, final Exception exception) {
        getInstance().addMeldung(new Meldung(Meldungstyp.FEHLER, error, exception));
    }

    public static void addHinweis(final String hinweis) {
        getInstance().addMeldung(new Meldung(Meldungstyp.HINWEIS, hinweis));
    }

    public static void addMetadaten(final String metadaten) {
        getInstance().addMeldung(new Meldung(Meldungstyp.METADATEN, metadaten));
    }

    public static boolean hasError() {
        return getInstance().getStatusLogEintraege().stream().anyMatch(Meldung::isError);
    }

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
            String filePathWithName = FileHandler.generateFile(filePath, "DebugLog", "txt");
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
