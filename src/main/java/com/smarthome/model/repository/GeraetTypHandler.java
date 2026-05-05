package com.smarthome.model.repository;

import com.smarthome.util.StatusLog;
import com.smarthome.util.customExceptions.NoGeraetProvidedException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public final class GeraetTypHandler {

    private static final String GERAETE_KLASSEN_PAKET = "com.smarthome.model.entity.geraete";

    public static List<Class<?>> getGeraeteKlassen() throws NoGeraetProvidedException {
        StatusLog.addHinweis("Beginne Geräteklassen zu laden");
        final InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(GERAETE_KLASSEN_PAKET.replaceAll("[.]", "/"));
        if (stream == null) throw new NoGeraetProvidedException("Es wurde keine Geräte Klasse gefunden");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(GeraetTypHandler::getClass)
                .collect(Collectors.<Class<?>>toList());
    }

    private static Class<?> getClass(final String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(GERAETE_KLASSEN_PAKET + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException eCNF) {
            StatusLog.addError(eCNF);
        }
        return clazz;
    }
}
