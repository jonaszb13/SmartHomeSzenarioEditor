package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

import java.util.Map;

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
    public void setValues(Map<String, String> map) {
        final String eingeschaltet = map.get("eingeschaltet");
        final String ausschlag = map.get("ausschlag");
        if (eingeschaltet != null) setEingeschaltet(Boolean.parseBoolean(eingeschaltet));
        else throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        if (ausschlag != null) setAusschlag(Boolean.parseBoolean(ausschlag));
        else throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
    }
}
