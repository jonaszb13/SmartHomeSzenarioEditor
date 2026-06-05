package data.models.fachobjekte.geraetearten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Merkmalbezeichnung;
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
    }

    public Steckdose(final UUID id, final String name, final Raum raum, boolean eingeschaltet, double aktuelleLeistung) {
        super(id, name, raum);
        this.eingeschaltet = eingeschaltet;
        this.aktuelleLeistung = aktuelleLeistung;
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
        if (Merkmalbezeichnung.EINGESCHALTET.getBezeichnung().equals(key)) {
            setEingeschaltet(Boolean.parseBoolean(value));
        } else if (Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung().equals(key)) {
            setAktuelleLeistung(Double.parseDouble(value));
        } else {
            final IllegalArgumentException iaE = new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
            StatusLog.addError(iaE.getMessage(), iaE);
            throw iaE;
        }
    }

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        values.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), Boolean.toString(isEingeschaltet()));
        values.put(Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung(), formatiereZahlenwerteInsDeutsche(Double.toString(getAktuelleLeistung())));
        return values;
    }

    @Override
    public Map<String, Class<?>> getAttributTypen() {
        final Map<String, Class<?>> typen = new HashMap<>();
        typen.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), boolean.class);
        typen.put(Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung(), double.class);
        return typen;
    }

    @Override
    public boolean isGueltigeAttribute(final Map<String, String> attributeMap) {
        if (attributeMap.get(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung()) == null
                || attributeMap.get(Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung()) == null) {
            return false;
        }
        attributeMap.replace(Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung(), formatiereZahlenwerteInsEnglische(attributeMap.get(Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung())));
        if (!isDouble(attributeMap.get(Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung()))) {
            StatusLog.addError("Die " + Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung() + " muss eine Zahl sein \nDer Dezimalpunkt ist das Komma");
            return false;
        }
        final double aktuelleLeistung = Double.parseDouble(attributeMap.get(Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung()));
        if (aktuelleLeistung < 0 || aktuelleLeistung > 3680) {
            StatusLog.addError("Die " + Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung() + " muss zwischen 0 und 3680 Watt liegen \nDer Dezimalpunkt ist das Komma");
            return false;
        }
        return true;
    }
}
