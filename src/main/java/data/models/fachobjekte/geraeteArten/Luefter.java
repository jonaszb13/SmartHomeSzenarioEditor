package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import util.statusmeldungen.StatusLog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Luefter extends Geraet {
    private boolean eingeschaltet;
    private double staerke;

    public Luefter(final UUID id, final String name, final Raum raum) {
        super(id, name, raum);
    }

    public boolean isEingeschaltet() {
        return eingeschaltet;
    }

    public void setEingeschaltet(final boolean eingeschaltet) {
        this.eingeschaltet = eingeschaltet;
    }

    public double getStaerke() {
        return staerke;
    }

    public void setStaerke(final double staerke) {
        this.staerke = staerke;
    }

    @Override
    public void updateValue(final String key, final String value) {
        switch (key) {
            case "eingeschaltet":
                setEingeschaltet(Boolean.parseBoolean(value));
                break;
            case "staerke":
                setStaerke(Double.parseDouble(value));
                break;
            default:
                throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        values.put("eingeschaltet", Boolean.toString(eingeschaltet));
        values.put("staerke", formatiereZahlenwerteInsDeutsche(Double.toString(staerke)));
        return values;
    }

    @Override
    public Map<String, Class<?>> getAttributTypen() {
        final Map<String, Class<?>> typen = new HashMap<>();
        typen.put("eingeschaltet", boolean.class);
        typen.put("staerke", double.class);
        return typen;
    }
    @Override
    public boolean isGueltigeAttribute(final Map<String, String> attributeMap) {
        if (attributeMap.get("eingeschaltet") == null
                || attributeMap.get("staerke") == null) {
            return false;
        }
        attributeMap.replace("staerke", formatiereZahlenwerteInsEnglische(attributeMap.get("staerke")));
        if (!isDouble(attributeMap.get("stärke"))) {
            StatusLog.addError("Die Stärke muss eine Zahl sein");
            return false;
        }

        double staerke = Double.parseDouble(attributeMap.get("stärke"));
        if (staerke < 0 || staerke > 100) {
            StatusLog.addError("Die Stärke muss zwischen 0 und 100 Prozent liegen");
            return false;
        }
        return true;
    }
}
