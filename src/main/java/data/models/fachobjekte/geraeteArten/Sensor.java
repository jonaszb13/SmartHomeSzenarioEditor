package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Sensor extends Geraet {

    private boolean eingeschaltet;
    private boolean ausschlag;

    public Sensor(final UUID id, final String name, final Raum raum) {
        super(id, name, raum);
    }

    public boolean isEingeschaltet() {
        return eingeschaltet;
    }

    public void setEingeschaltet(final boolean eingeschaltet) {
        this.eingeschaltet = eingeschaltet;
    }

    public boolean isAusschlag() {
        return ausschlag;
    }

    public void setAusschlag(final boolean ausschlag) {
        this.ausschlag = ausschlag;
    }

    @Override
    public void updateValue(final String key, final String value) {
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

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        values.put("eingeschaltet", Boolean.toString(eingeschaltet));
        values.put("ausschlag", Boolean.toString(ausschlag));
        return values;
    }
}
