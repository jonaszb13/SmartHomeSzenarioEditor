package data;

import data.daos.Geraet;
import data.daos.Raum;
import util.DebugLog;
import util.customExceptions.NoGeraetProvidedException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory-Klasse die das dynamische erstellen von Objekten, welche von Geraet erben, ermöglicht
 * @author Ben Knirsch
 */
public final class GeraetFactory {

    private static GeraetFactory instance;
    private Map<String, Class> geraeteKlassen;


    private GeraetFactory() {
    }

    public static GeraetFactory getInstance() throws NoGeraetProvidedException {
        if (instance == null) {
            instance = new GeraetFactory();
            instance.geraeteKlassen = new HashMap<>();
            final List<Class> geraeteKlassenList = GeraetTypHandler.getGeraeteKlassen();
            for (final Class aClass : geraeteKlassenList) {
                instance.geraeteKlassen.put(aClass.getName(), aClass);
            }
        }
        return instance;
    }

    public Geraet createGeraet(int id, String name, Raum raum, String typ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Geraet geraet = null;
        try {
            geraet = (Geraet) geraeteKlassen.get("data.daos.geraete." + typ)
                    .getDeclaredConstructor(int.class, String.class, Raum.class)
                    .newInstance(id, name, raum);
        } catch (ClassCastException eCC) {
            DebugLog.addError("Es befindet sich eine Klasse im Gerätetypen-Ordner die nicht von Gerät erbt", eCC);
        }
        return geraet;
    }

}
