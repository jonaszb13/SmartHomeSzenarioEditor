package data.services;

import util.customExceptions.NoGeraetProvidedException;
import util.statusmeldungen.StatusLog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Utility Klasse, die zur Laufzeit ermittelt,
 * welche Klassen im geraete Paket liegen
 *
 * @author Ben Knirsch
 */
public final class GeraetTypHandler {

    private static final String GERAETE_PAKET = "data.models.fachobjekte.geraeteArten";
    private static final String GERAETE_LISTE = GERAETE_PAKET.replaceAll("[.]", "/") + "/geraete.txt";

    private GeraetTypHandler() {
    }

    /**
     * @return Liste aller Klassen, in dem übergeben Paket
     * @throws NoGeraetProvidedException Wird geworfen, wenn der Ordner leer ist
     */
    public static List<Class<?>> getGeraeteKlassen() throws NoGeraetProvidedException {
        StatusLog.addHinweis("Beginne Geräteklassen zu laden");
        InputStream stream = GeraetTypHandler.class.getClassLoader().getResourceAsStream(GERAETE_LISTE);
        // Fallback: wenn die Anwendung aus einer Standalone Version gestartet wird
        //TODO @Jonas wollen wir die jetzt noch tauschen,
        // damit in der IDE dynamisch geladen werden kann?
        if (stream == null) {
            stream = ClassLoader.getSystemClassLoader().getResourceAsStream(GERAETE_PAKET.replaceAll("[.]", "/"));
        }
        if (stream == null) throw new NoGeraetProvidedException("Es wurde keine Geräte Klasse gefunden");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        List<Class<?>> classes = reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(GeraetTypHandler::getClass)
                .collect(Collectors.<Class<?>>toList());
        StatusLog.addHinweis("Geräteklassen erfolgreich geladen");
        return classes;
    }

    /**
     * Subklasse für Fehlerhandling im Lamda-Ausdruck
     *
     * @param className Name der Klasse die gefunden werden soll
     * @return gefundene Klasse
     */
    private static Class<?> getClass(final String className) {
        Class<?> clazz = null;
        try {
            //KEY: Klassenname ohne Dateiendung
            clazz = Class.forName(GERAETE_PAKET + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException eCNF) {
            StatusLog.addError(eCNF);
        }
        return clazz;
    }

}
