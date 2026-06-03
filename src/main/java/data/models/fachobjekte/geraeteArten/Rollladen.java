package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import util.statusmeldungen.StatusLog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Rollladen extends Geraet {

    private double schliessstatus;
    private double neigung;

    public Rollladen(final UUID id, final String name, final Raum raum) {
        super(id, name, raum);
    }

    public double getSchliessstatus() {
        return schliessstatus;
    }

    public void setSchliessstatus(final double schliessstatus) {
        this.schliessstatus = schliessstatus;
    }

    public double getNeigung() {
        return neigung;
    }

    public void setNeigung(final double neigung) {
        this.neigung = neigung;
    }

    @Override
    public void updateValue(final String key, final String value) {
        switch (key) {
            case "schliessstatus":
                setSchliessstatus(Double.parseDouble(value));
                break;
            case "neigung":
                setNeigung(Double.parseDouble(value));
                break;
            default:
                throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        values.put("schliessstatus", formatiereZahlenwerteInsDeutsche(Double.toString(getSchliessstatus())));
        values.put("neigung", formatiereZahlenwerteInsDeutsche(Double.toString(getNeigung())));
        return values;
    }

    @Override
    public Map<String, Class<?>> getAttributTypen() {
        final Map<String, Class<?>> typen = new HashMap<>();
        typen.put("schliessstatus", double.class);
        typen.put("neigung", double.class);
        return typen;
    }
    @Override
    public boolean isGueltigeAttribute(final Map<String, String> attributeMap) {
        if (attributeMap.get("schliessstatus") == null
                || attributeMap.get("neigung") == null) {
            return false;
        }
        attributeMap.replace("schliessstatus", formatiereZahlenwerteInsEnglische(attributeMap.get("schliessstatus")));
        if (!isDouble(attributeMap.get("schliessstatus"))) {
            StatusLog.addError("Der Schließstatus muss eine Zahl sein");
            return false;
        }
        attributeMap.replace("neigung", formatiereZahlenwerteInsEnglische(attributeMap.get("neigung")));
        if (!isDouble(attributeMap.get("neigung"))) {
            StatusLog.addError("Die Neigung muss eine Zahl sein");
            return false;
        }

        double schliessstatus = Double.parseDouble(attributeMap.get("schliessstatus"));
        if (schliessstatus < 0 || schliessstatus > 100) {
            StatusLog.addError("Der Schließstatus muss zwischen 0 und 100 Prozent liegen");
            return false;
        }

        double neigung = Double.parseDouble(attributeMap.get("neigung"));
        if (neigung < -90 || neigung > 90) {
            StatusLog.addError("Die Neigung muss zwischen -90 und +90 liegen");
            return false;
        }
        return true;
    }
}
