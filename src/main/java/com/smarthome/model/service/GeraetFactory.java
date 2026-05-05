package com.smarthome.model.service;

import com.smarthome.model.entity.Geraet;
import com.smarthome.model.entity.Raum;
import com.smarthome.model.repository.GeraetTypHandler;
import com.smarthome.util.StatusLog;
import com.smarthome.util.customExceptions.NoGeraetProvidedException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class GeraetFactory {

    private static GeraetFactory instance;
    private Map<String, Class<?>> geraeteKlassen;

    private GeraetFactory() {
    }

    public static GeraetFactory getInstance() throws NoGeraetProvidedException {
        if (instance == null) {
            instance = new GeraetFactory();
            instance.geraeteKlassen = new HashMap<>();
            final List<Class<?>> geraeteKlassenList = GeraetTypHandler.getGeraeteKlassen();
            for (final Class<?> aClass : geraeteKlassenList) {
                instance.geraeteKlassen.put(aClass.getName().substring(aClass.getName().lastIndexOf('.') + 1), aClass);
            }
        }
        return instance;
    }

    public Geraet createGeraet(final UUID id, final String name, final Raum raum, final String typ)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Geraet geraet = null;
        try {
            geraet = (Geraet) geraeteKlassen.get(typ)
                    .getDeclaredConstructor(UUID.class, String.class, Raum.class)
                    .newInstance(id, name, raum);
        } catch (ClassCastException eCC) {
            StatusLog.addError("Es befindet sich eine Klasse im Gerätetypen-Ordner die nicht von Gerät erbt", eCC);
        } catch (NullPointerException eNP) {
            StatusLog.addError("In der Datenbank vorhandene Klasse konnte nicht gefunden werden", eNP);
        }
        return geraet;
    }
}
