package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

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
}
