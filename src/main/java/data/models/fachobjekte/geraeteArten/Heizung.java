package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Heizung extends Geraet {
    private float zielTemp;

    public Heizung(final UUID id, final String name, final Raum raum, final float zielTemp) {
        super(id, name, raum);
        this.zielTemp = zielTemp;
    }

    public Heizung(final UUID id, final String name, final Raum raum) {
        super(id, name, raum);
    }

    public float getZielTemp() {
        return zielTemp;
    }

    public void setZielTemp(final float zielTemp) {
        this.zielTemp = zielTemp;
    }

    @Override
    public void updateValue(final String key, final String value) {
        if ("zielTemp".equals(key)) {
            setZielTemp(Float.parseFloat(value));
        } else {
            throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        values.put("zielTemp", Float.toString(zielTemp));
        return values;
    }
}
