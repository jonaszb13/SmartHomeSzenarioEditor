package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

public class Heizung extends Geraet {
    private float zielTemp;

    public Heizung(int id, String name, Raum raum, float zielTemp) {
        super(id, name, raum);
        this.zielTemp = zielTemp;
    }

    public Heizung(int id, String name, Raum raum) {
        super(id, name, raum);
    }

    public float getZielTemp() {
        return zielTemp;
    }

    public void setZielTemp(float zielTemp) {
        this.zielTemp = zielTemp;
    }

    @Override
    public void updateValue(String key, String value) {
        if (key.equals("zielTemp")) {
            setZielTemp(Float.parseFloat(value));
        } else {
            throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }
}
