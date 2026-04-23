package data;

import util.DebugLog;
import util.customExceptions.NoGeraetProvidedException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;


public class GeraetTypHandler {

    private static final String geraeteKlassenPaket = "data.daos.geraete";


    /**
     * @return Liste aller Klassen, in dem übergeben Paket
     * @throws NoGeraetProvidedException Wird geworfen, wenn der Ordner leer ist
     */
     static List<Class> getGeraeteKlassen() throws NoGeraetProvidedException {
        final InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(geraeteKlassenPaket.replaceAll("[.]", "/"));
        if (stream == null) throw new NoGeraetProvidedException("Es wurde keine Geräte Klasse gefunden");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, geraeteKlassenPaket))
                .collect(Collectors.toList());
    }

    /**
     * Subklasse für Fehlerhandling im Lamda-Ausdruck
     * @param className Name der Klasse die gefunden werden soll
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
