package data.models.fachobjekte.geraetearten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Merkmalbezeichnung;
import data.models.fachobjekte.Raum;
import util.statusmeldungen.StatusLog;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Lampe extends Geraet {
    private double helligkeit;
    private Color farbe;
    private boolean eingeschaltet;

    public Lampe(final UUID id, final String name, final Raum raum, final double helligkeit, final Color farbe, final boolean eingeschaltet) {
        super(id, name, raum);
        this.helligkeit = helligkeit;
        this.farbe = farbe;
        this.eingeschaltet = eingeschaltet;
    }

    public Lampe(final UUID id, final String name, final Raum raum) {
        super(id, name, raum);
    }

    public double getHelligkeit() {
        return helligkeit;
    }

    public void setHelligkeit(final double helligkeit) {
        this.helligkeit = helligkeit;
    }

    public Color getFarbe() {
        return farbe;
    }

    public void setFarbe(final Color farbe) {
        this.farbe = farbe;
    }

    public boolean isEingeschaltet() {
        return eingeschaltet;
    }

    public void setEingeschaltet(final boolean eingeschaltet) {
        this.eingeschaltet = eingeschaltet;
    }

    @Override
    public void updateValue(final String key, final String value) {
        if (Merkmalbezeichnung.EINGESCHALTET.getBezeichnung().equals(key)) {
            setEingeschaltet(Boolean.parseBoolean(value));
        } else if (Merkmalbezeichnung.HELLIGKEIT.getBezeichnung().equals(key)) {
            setHelligkeit(Double.parseDouble(value));
        } else if (Merkmalbezeichnung.FARBE.getBezeichnung().equals(key)) {
            setFarbe(Color.decode(value));
        } else {
            final IllegalArgumentException iaE = new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
            StatusLog.addError(iaE.getMessage(), iaE);
            throw iaE;
        }
    }

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        values.put(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung(), formatiereZahlenwerteInsDeutsche(Double.toString(getHelligkeit())));
        values.put(Merkmalbezeichnung.FARBE.getBezeichnung(), farbe != null ? String.format("#%02x%02x%02x", farbe.getRed(), farbe.getGreen(), farbe.getBlue()) : "#000000");
        values.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), Boolean.toString(eingeschaltet));
        return values;
    }

    @Override
    public Map<String, Class<?>> getAttributTypen() {
        final Map<String, Class<?>> typen = new HashMap<>();
        typen.put(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung(), double.class);
        typen.put(Merkmalbezeichnung.FARBE.getBezeichnung(), Color.class);
        typen.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), boolean.class);
        return typen;
    }

    @Override
    public boolean isGueltigeAttribute(final Map<String, String> attributeMap) {
        if (attributeMap.get(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung()) == null
                || attributeMap.get(Merkmalbezeichnung.FARBE.getBezeichnung()) == null
                || attributeMap.get(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung()) == null) {
            return false;
        }
        attributeMap.replace(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung(), formatiereZahlenwerteInsEnglische(attributeMap.get(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung())));
        if (!isDouble(attributeMap.get(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung()))) {
            StatusLog.addError("Die " + Merkmalbezeichnung.HELLIGKEIT.getBezeichnung() + " muss eine Zahl sein \nDer Dezimalpunkt ist das Komma");
            return false;
        }

        final double helligkeit = Double.parseDouble(attributeMap.get(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung()));
        if (helligkeit < 0 || helligkeit > 100) {
            StatusLog.addError("Die " + Merkmalbezeichnung.HELLIGKEIT.getBezeichnung() + " muss zwischen 0 und 100 Prozent liegen \nDer Dezimalpunkt ist das Komma");
            return false;
        }
        return true;
    }
}
