package data;

import util.DebugLog;
import util.customExceptions.NoGeraetProvidedException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Statische Klasse, die zur Laufzeit ermittelt welche Klassen im geraete Paket liegen
 * @author Ben Knirsch
 */
public final class GeraetTypHandler {

    private static final String GERAETE_KLASSEN_PAKET = "data.daos.geraete";


    /**
     * @return Liste aller Klassen, in dem übergeben Paket
     * @throws NoGeraetProvidedException Wird geworfen, wenn der Ordner leer ist
     */
    /* package */
    static List<Class> getGeraeteKlassen() throws NoGeraetProvidedException {
        final InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(GERAETE_KLASSEN_PAKET.replaceAll("[.]", "/"));
        if (stream == null) throw new NoGeraetProvidedException("Es wurde keine Geräte Klasse gefunden");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, GERAETE_KLASSEN_PAKET))
                .collect(Collectors.toList());
    }

    /**
     * Subklasse für Fehlerhandling im Lamda-Ausdruck
     * @param className   Name der Klasse die gefunden werden soll
     * @param packageName Paket in dem die Klassen aller Geräte liegen
     * @return gefundene Klasse
     */
    private static Class getClass(String className, String packageName) {
        Class clazz = null;
        try {
            clazz = Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException eCNF) {
            DebugLog.addError(eCNF);
        }
        return clazz;
    }

}
