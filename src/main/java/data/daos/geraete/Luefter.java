package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

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
    public void updateValue(String key, String value) {
        switch (key) {
            case "eingeschaltet":
                setEingeschaltet(Boolean.parseBoolean(value));
                break;
            case "staerke":
                setStaerke(Float.parseFloat(value));
                break;
            default:
                throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }
}
