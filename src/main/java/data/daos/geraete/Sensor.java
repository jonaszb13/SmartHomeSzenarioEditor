package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

public class Sensor extends Geraet {

    private boolean eingeschaltet;
    private boolean ausschlag;

    public Sensor(int id, String name, Raum raum) {
        super(id, name, raum);
    }

    public boolean isEingeschaltet() {
        return eingeschaltet;
    }

    public void setEingeschaltet(boolean eingeschaltet) {
        this.eingeschaltet = eingeschaltet;
    }

    public boolean isAusschlag() {
        return ausschlag;
    }

    public void setAusschlag(boolean ausschlag) {
        this.ausschlag = ausschlag;
    }

    @Override
    public void updateValue(String key, String value) {
        switch (key) {
            case "eingeschaltet":
                setEingeschaltet(Boolean.parseBoolean(value));
                break;
            case "ausschlag":
                setAusschlag(Boolean.parseBoolean(value));
                break;
            default:
                throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }
}
