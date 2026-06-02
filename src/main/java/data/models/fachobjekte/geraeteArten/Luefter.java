package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Luefter extends Geraet {
    private boolean eingeschaltet;
    private float staerke;

    public Luefter(final UUID id, final String name, final Raum raum) {
        super(id, name, raum);
    }

    public boolean isEingeschaltet() {
        return eingeschaltet;
    }

    public void setEingeschaltet(final boolean eingeschaltet) {
        this.eingeschaltet = eingeschaltet;
    }

    public float getStaerke() {
        return staerke;
    }

    public void setStaerke(final float staerke) {
        this.staerke = staerke;
    }

    @Override
    public void updateValue(final String key, final String value) {
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

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        values.put("eingeschaltet", Boolean.toString(eingeschaltet));
        values.put("staerke", Float.toString(staerke));
        return values;
    }

    @Override
    public Map<String, Class<?>> getAttributTypen() {
        final Map<String, Class<?>> typen = new HashMap<>();
        typen.put("eingeschaltet", boolean.class);
        typen.put("staerke", float.class);
        return typen;
    }
}
