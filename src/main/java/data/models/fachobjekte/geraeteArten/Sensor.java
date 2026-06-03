package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Merkmalbezeichnung;
import data.models.fachobjekte.Raum;
import util.statusmeldungen.StatusLog;

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
        if (Merkmalbezeichnung.EINGESCHALTET.getBezeichnung().equals(key)) {
            setEingeschaltet(Boolean.parseBoolean(value));
        } else if (Merkmalbezeichnung.AUSSCHLAG.getBezeichnung().equals(key)) {
            setAusschlag(Boolean.parseBoolean(value));
        } else {
            IllegalArgumentException iaE = new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
            StatusLog.addError(iaE.getMessage(), iaE);
            throw iaE;
        }
    }

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        values.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), Boolean.toString(eingeschaltet));
        values.put(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung(), Boolean.toString(ausschlag));
        return values;
    }

    @Override
    public Map<String, Class<?>> getAttributTypen() {
        final Map<String, Class<?>> typen = new HashMap<>();
        typen.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), boolean.class);
        typen.put(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung(), boolean.class);
        return typen;
    }

    @Override
    public boolean isGueltigeAttribute(final Map<String, String> attributeMap) {
        return attributeMap.get(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung()) != null
                && attributeMap.get(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung()) != null;
    }
}
