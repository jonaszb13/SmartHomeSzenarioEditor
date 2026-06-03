package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Merkmalbezeichnung;
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
        if (Merkmalbezeichnung.EINGESCHALTET.getBezeichnung().equals(key)) {
            setEingeschaltet(Boolean.parseBoolean(value));
        } else if (Merkmalbezeichnung.STAERKE.getBezeichnung().equals(key)) {
            setStaerke(Double.parseDouble(value));
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
        values.put(Merkmalbezeichnung.STAERKE.getBezeichnung(), formatiereZahlenwerteInsDeutsche(Double.toString(staerke)));
        return values;
    }

    @Override
    public Map<String, Class<?>> getAttributTypen() {
        final Map<String, Class<?>> typen = new HashMap<>();
        typen.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), boolean.class);
        typen.put(Merkmalbezeichnung.STAERKE.getBezeichnung(), double.class);
        return typen;
    }

    @Override
    public boolean isGueltigeAttribute(final Map<String, String> attributeMap) {
        if (attributeMap.get(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung()) == null
                || attributeMap.get(Merkmalbezeichnung.STAERKE.getBezeichnung()) == null) {
            return false;
        }
        attributeMap.replace(Merkmalbezeichnung.STAERKE.getBezeichnung(), formatiereZahlenwerteInsEnglische(attributeMap.get(Merkmalbezeichnung.STAERKE.getBezeichnung())));
        if (!isDouble(attributeMap.get(Merkmalbezeichnung.STAERKE.getBezeichnung()))) {
            StatusLog.addError("Die " + Merkmalbezeichnung.STAERKE.getBezeichnung() + " muss eine Zahl sein \nDer Dezimalpunkt ist das Komma");
            return false;
        }

        double staerke = Double.parseDouble(attributeMap.get(Merkmalbezeichnung.STAERKE.getBezeichnung()));
        if (staerke < 0 || staerke > 100) {
            StatusLog.addError("Die " + Merkmalbezeichnung.STAERKE.getBezeichnung() + " muss zwischen 0 und 100 Prozent liegen \nDer Dezimalpunkt ist das Komma");
            return false;
        }
        return true;
    }
}
