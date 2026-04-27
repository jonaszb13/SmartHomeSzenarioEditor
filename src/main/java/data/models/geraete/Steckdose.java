package data.models.geraete;

import data.models.Geraet;
import data.models.Raum;

import java.util.UUID;

public class Steckdose extends Geraet {

    private boolean eingeschaltet;
    private float aktuelleLeistung;

    public Steckdose(UUID id, String name, Raum raum) {
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
    public void updateValue(String key, String value) {
        switch (key) {
            case "eingeschaltet":
                setEingeschaltet(Boolean.parseBoolean(value));
                break;
            case "aktuelleLeistung":
                setAktuelleLeistung(Float.parseFloat(value));
                break;
            default:
                throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }
}
