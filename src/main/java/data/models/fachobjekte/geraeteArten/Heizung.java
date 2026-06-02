package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import util.statusmeldungen.StatusLog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Heizung extends Geraet {
    private double zielTemp;

    public Heizung(final UUID id, final String name, final Raum raum, final double zielTemp) {
        super(id, name, raum);
        this.zielTemp = zielTemp;
    }

    public Heizung(final UUID id, final String name, final Raum raum) {
        super(id, name, raum);
    }

    public double getZielTemp() {
        return zielTemp;
    }

    public void setZielTemp(final double zielTemp) {
        this.zielTemp = zielTemp;
    }

    @Override
    public void updateValue(final String key, final String value) {
        if ("zielTemp".equals(key)) {
            setZielTemp(Double.parseDouble(value));
        } else {
            throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        values.put("zielTemp", formatiereZahlenwerteInsDeutsche(Double.toString(zielTemp)));
        return values;
    }

    @Override
    public Map<String, Class<?>> getAttributTypen() {
        final Map<String, Class<?>> typen = new HashMap<>();
        typen.put("zielTemp", double.class);
        return typen;
    }

    @Override
    public boolean isGueltigeAttribute(final Map<String, String> attributeMap) {
        if (attributeMap.get("zielTemp") == null) return false;
        attributeMap.replace("zielTemp", formatiereZahlenwerteInsEnglische(attributeMap.get("zielTemp")));
        if (!isDouble(attributeMap.get("zielTemp"))) {
            StatusLog.addError("Die Zieltemperatur muss eine Zahl sein");
            return false;
        }
        double zielTemp = Double.parseDouble(attributeMap.get("zielTemp"));
        if (zielTemp < 5 || zielTemp > 30) {
            StatusLog.addError("Die Zieltemperatur muss zwischen 5 und 30 Grad Celsius liegen");
            return false;
        }
        return true;
    }
}
