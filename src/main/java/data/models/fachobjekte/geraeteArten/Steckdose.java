package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;

import java.util.UUID;

public class Steckdose extends Geraet {

    private boolean eingeschaltet;
    private float aktuelleLeistung;

    public Steckdose(final UUID id, final String name, final Raum raum) {
        super(id, name, raum);
        eingeschaltet = false;
    }

    public boolean isEingeschaltet() {
        return eingeschaltet;
    }

    public void setEingeschaltet(final boolean eingeschaltet) {
        this.eingeschaltet = eingeschaltet;
    }

    public float getAktuelleLeistung() {
        return aktuelleLeistung;
    }

    public void setAktuelleLeistung(final float aktuelleLeistung) {
        this.aktuelleLeistung = aktuelleLeistung;
    }

    @Override
    public void updateValue(final String key, final String value) {
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
