package data;

import util.DebugLog;
import util.customExceptions.NoGeraetProvidedException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Utility Klasse, die zur Laufzeit ermittelt,
 * welche Klassen im geraete Paket liegen
 * @author Ben Knirsch
 */
public final class GeraetTypHandler {

    private static final String GERAETE_KLASSEN_PAKET = "data.models.geraete";


    /**
     * @return Liste aller Klassen, in dem übergeben Paket
     * @throws NoGeraetProvidedException Wird geworfen, wenn der Ordner leer ist
     */
    /* package */
    static List<Class<?>> getGeraeteKlassen() throws NoGeraetProvidedException {
        DebugLog.addHinweis("Beginne Geräteklassen zu laden");
        final InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(GERAETE_KLASSEN_PAKET.replaceAll("[.]", "/"));
        if (stream == null) throw new NoGeraetProvidedException("Es wurde keine Geräte Klasse gefunden");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(GeraetTypHandler::getClass)
                .collect(Collectors.<Class<?>>toList());
    }

    /**
     * Subklasse für Fehlerhandling im Lamda-Ausdruck
     * @param className Name der Klasse die gefunden werden soll
     * @return gefundene Klasse
     */
    private static Class<?> getClass(final String className) {
        Class<?> clazz = null;
        try {
            //KEY: Klassenname ohne Dateiendung
            clazz = Class.forName(GERAETE_KLASSEN_PAKET + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException eCNF) {
            DebugLog.addError(eCNF);
        }
        return clazz;
    }

}
