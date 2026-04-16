package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

import java.util.HashMap;

public class Heizung extends Geraet {
    private float zielTemp;

    public Heizung(int id, String name, Raum raum, float zielTemp) {
        super(id, name, raum);
        this.zielTemp = zielTemp;
    }

    public Heizung(int id, String name, Raum raum) {
        super(id, name, raum);
    }

    @Override
    public void setValues(HashMap<String, String> hashMap) throws IllegalArgumentException {
        String zielTempString = hashMap.get("zielTemp");
        if (zielTempString == null) throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        else this.zielTemp = Float.parseFloat(zielTempString);
    }

    public float getZielTemp() {
        return zielTemp;
    }

    public void setZielTemp(float zielTemp) {
        this.zielTemp = zielTemp;
    }
}
