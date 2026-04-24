package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

import java.util.Map;

public class Steckdose extends Geraet {

    private boolean eingeschaltet;
    private float aktuelleLeistung;

    public Steckdose(int id, String name, Raum raum) {
        super(id, name, raum);
        eingeschaltet = false;
    }

    public boolean isEingeschaltet() {
        return eingeschaltet;
    }

    public void setEingeschaltet(boolean eingeschaltet) {
        this.eingeschaltet = eingeschaltet;
    }

    public float getAktuelleLeistung() {
        return aktuelleLeistung;
    }

    public void setAktuelleLeistung(float aktuelleLeistung) {
        this.aktuelleLeistung = aktuelleLeistung;
    }

    @Override
    public void setValues (Map<String, String > map) throws IllegalArgumentException {
        final String strom = map.get("Strom");
        if (strom == null) throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        else this.eingeschaltet = Boolean.parseBoolean(strom);
    }
}
