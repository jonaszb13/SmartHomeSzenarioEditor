package data.models.fachobjekte.geraete_arten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Merkmalbezeichnung;
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
        if (Merkmalbezeichnung.ZIELTEMP.getBezeichnung().equals(key)) {
            setZielTemp(Double.parseDouble(value));
        } else {
            final IllegalArgumentException iaE = new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
            StatusLog.addError(iaE.getMessage(), iaE);
            throw iaE;
        }
    }

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        values.put(Merkmalbezeichnung.ZIELTEMP.getBezeichnung(), formatiereZahlenwerteInsDeutsche(Double.toString(getZielTemp())));
        return values;
    }

    @Override
    public Map<String, Class<?>> getAttributTypen() {
        final Map<String, Class<?>> typen = new HashMap<>();
        typen.put(Merkmalbezeichnung.ZIELTEMP.getBezeichnung(), double.class);
        return typen;
    }

    @Override
    public boolean isGueltigeAttribute(final Map<String, String> attributeMap) {
        if (attributeMap.get(Merkmalbezeichnung.ZIELTEMP.getBezeichnung()) == null) return false;
        attributeMap.replace(Merkmalbezeichnung.ZIELTEMP.getBezeichnung(), formatiereZahlenwerteInsEnglische(attributeMap.get(Merkmalbezeichnung.ZIELTEMP.getBezeichnung())));
        if (!isDouble(attributeMap.get(Merkmalbezeichnung.ZIELTEMP.getBezeichnung()))) {
            StatusLog.addError("Die " + Merkmalbezeichnung.ZIELTEMP.getBezeichnung() + " muss eine Zahl sein \nDer Dezimalpunkt ist das Komma");
            return false;
        }
        final double zielTemp = Double.parseDouble(attributeMap.get(Merkmalbezeichnung.ZIELTEMP.getBezeichnung()));
        if (zielTemp < 5 || zielTemp > 30) {
            StatusLog.addError("Die " + Merkmalbezeichnung.ZIELTEMP.getBezeichnung() + " muss zwischen 5 und 30 Grad Celsius liegen \nDer Dezimalpunkt ist das Komma");
            return false;
        }
        return true;
    }
}
