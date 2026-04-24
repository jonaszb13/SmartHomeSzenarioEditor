package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

import java.util.Map;

public class Luefter extends Geraet {
    private boolean eingeschaltet;
    private float staerke;

    public Luefter(int id, String name, Raum raum) {
        super(id, name, raum);
    }

    public boolean isEingeschaltet() {
        return eingeschaltet;
    }

    public void setEingeschaltet(boolean eingeschaltet) {
        this.eingeschaltet = eingeschaltet;
    }

    public float getStaerke() {
        return staerke;
    }

    public void setStaerke(float staerke) {
        this.staerke = staerke;
    }

    @Override
    public void setValues(Map<String, String> map) throws IllegalArgumentException {
        final String eingschaltet = map.get("eingschaltet");
        final String staerke = map.get("staerke");
        if (eingschaltet != null) setEingeschaltet(Boolean.parseBoolean(eingschaltet));
        else throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        if (staerke != null) setStaerke(Float.parseFloat(staerke));
        else throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
    }

}
