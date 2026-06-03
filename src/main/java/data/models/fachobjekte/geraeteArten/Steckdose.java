package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import util.statusmeldungen.StatusLog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Steckdose extends Geraet {

    private boolean eingeschaltet;
    private double aktuelleLeistung;

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

    public double getAktuelleLeistung() {
        return aktuelleLeistung;
    }

    public void setAktuelleLeistung(final double aktuelleLeistung) {
        this.aktuelleLeistung = aktuelleLeistung;
    }

    @Override
    public void updateValue(final String key, final String value) {
        switch (key) {
            case "eingeschaltet":
                setEingeschaltet(Boolean.parseBoolean(value));
                break;
            case "aktuelleLeistung":
                setAktuelleLeistung(Double.parseDouble(value));
                break;
            default:
                throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        values.put("eingeschaltet", Boolean.toString(eingeschaltet));
        values.put("aktuelleLeistung", formatiereZahlenwerteInsDeutsche(Double.toString(aktuelleLeistung)));
        return values;
    }

    @Override
    public Map<String, Class<?>> getAttributTypen() {
        final Map<String, Class<?>> typen = new HashMap<>();
        typen.put("eingeschaltet", boolean.class);
        typen.put("aktuelleLeistung", double.class);
        return typen;
    }
    @Override
    public boolean isGueltigeAttribute(final Map<String, String> attributeMap) {
        if (attributeMap.get("eingeschaltet") == null
                || attributeMap.get("aktuelleLeistung") == null) {
            return false;
        }
        attributeMap.replace("aktuelleLeistung", formatiereZahlenwerteInsEnglische(attributeMap.get("aktuelleLeistung")));
        if (!isDouble(attributeMap.get("aktuelleLeistung"))) {
            StatusLog.addError("Die aktuelle Leistung muss eine Zahl sein");
            return false;
        }
        double aktuelleLeistung = Double.parseDouble(attributeMap.get("aktuelleLeistung"));
        if (aktuelleLeistung < 0 || aktuelleLeistung > 3680) {
            StatusLog.addError("Die aktuelle Leistung muss zwischen 0 und 3680 Watt liegen");
            return false;
        }
        return true;
    }
}
