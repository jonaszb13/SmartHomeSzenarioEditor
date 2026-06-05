package data.models.fachobjekte.geraetearten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Merkmalbezeichnung;
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
    public Rollladen(final UUID id, final String name, final Raum raum, double schliessstatus, double neigung) {
        super(id, name, raum);
        this.schliessstatus = schliessstatus;
        this.neigung = neigung;
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
        if (Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung().equals(key)) {
            setSchliessstatus(Double.parseDouble(value));
        } else if (Merkmalbezeichnung.NEIGUNG.getBezeichnung().equals(key)) {
            setNeigung(Double.parseDouble(value));
        } else {
            final IllegalArgumentException iaE = new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
            StatusLog.addError(iaE.getMessage(), iaE);
            throw iaE;
        }
    }

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        values.put(Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung(), formatiereZahlenwerteInsDeutsche(Double.toString(getSchliessstatus())));
        values.put(Merkmalbezeichnung.NEIGUNG.getBezeichnung(), formatiereZahlenwerteInsDeutsche(Double.toString(getNeigung())));
        return values;
    }

    @Override
    public Map<String, Class<?>> getAttributTypen() {
        final Map<String, Class<?>> typen = new HashMap<>();
        typen.put(Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung(), double.class);
        typen.put(Merkmalbezeichnung.NEIGUNG.getBezeichnung(), double.class);
        return typen;
    }

    @Override
    public boolean isGueltigeAttribute(final Map<String, String> attributeMap) {
        if (attributeMap.get(Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung()) == null
                || attributeMap.get(Merkmalbezeichnung.NEIGUNG.getBezeichnung()) == null) {
            return false;
        }
        attributeMap.replace(Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung(), formatiereZahlenwerteInsEnglische(attributeMap.get(Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung())));
        if (!isDouble(attributeMap.get(Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung()))) {
            StatusLog.addError("Der " + Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung() + " muss eine Zahl sein \nDer Dezimalpunkt ist das Komma");
            return false;
        }
        attributeMap.replace(Merkmalbezeichnung.NEIGUNG.getBezeichnung(), formatiereZahlenwerteInsEnglische(attributeMap.get(Merkmalbezeichnung.NEIGUNG.getBezeichnung())));
        if (!isDouble(attributeMap.get(Merkmalbezeichnung.NEIGUNG.getBezeichnung()))) {
            StatusLog.addError("Die " + Merkmalbezeichnung.NEIGUNG.getBezeichnung() + " muss eine Zahl sein \nDer Dezimalpunkt ist das Komma");
            return false;
        }

        final double schliessstatus = Double.parseDouble(attributeMap.get(Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung()));
        if (schliessstatus < 0 || schliessstatus > 100) {
            StatusLog.addError("Der " + Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung() + " muss zwischen 0 und 100 Prozent liegen \nDer Dezimalpunkt ist das Komma");
            return false;
        }

        final double neigung = Double.parseDouble(attributeMap.get(Merkmalbezeichnung.NEIGUNG.getBezeichnung()));
        if (neigung < -90 || neigung > 90) {
            StatusLog.addError("Die " + Merkmalbezeichnung.NEIGUNG.getBezeichnung() + " muss zwischen -90 und +90 liegen \nDer Dezimalpunkt ist das Komma");
            return false;
        }
        return true;
    }
}
