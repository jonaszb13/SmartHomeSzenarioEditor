package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
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
        switch (key) {
            case "eingeschaltet":
                setEingeschaltet(Boolean.parseBoolean(value));
                break;
            case "helligkeit":
                setHelligkeit(Double.parseDouble(value));
                break;
            case "farbe":
                setFarbe(Color.decode(value));
                break;
            default:
                IllegalArgumentException iaE = new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
                StatusLog.addError(iaE.getMessage(), iaE);
                throw iaE;
        }
    }

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        //TODO Keys etc. als Enums einführen
        values.put("helligkeit", bereinigeZahlenwerteInsDeutsche(Double.toString(getHelligkeit())));
        values.put("farbe", farbe != null ? String.format("#%02x%02x%02x", farbe.getRed(), farbe.getGreen(), farbe.getBlue()) : "#000000");
        values.put("eingeschaltet", Boolean.toString(eingeschaltet));
        return values;
    }

    @Override
    public Map<String, Class<?>> getAttributTypen() {
        final Map<String, Class<?>> typen = new HashMap<>();
        typen.put("helligkeit", double.class);
        typen.put("farbe", Color.class);
        typen.put("eingeschaltet", boolean.class);
        return typen;
    }
    @Override
    public boolean isGueltigeAttribute(final Map<String, String> attributeMap) {
        if (attributeMap.get("helligkeit") == null
                || attributeMap.get("farbe") == null
                || attributeMap.get("eingeschaltet") == null) {
            return false;
        }
        attributeMap.replace("helligkeit", bereinigeZahlenwerteInsEnglische(attributeMap.get("helligkeit")));
        if (!isDouble(attributeMap.get("helligkeit"))) {
            StatusLog.addError("Die Helligkeit muss eine Zahl sein");
            return false;
        }

        double helligkeit = Double.parseDouble(attributeMap.get("helligkeit"));
        if (helligkeit < 0 || helligkeit > 100) {
            StatusLog.addError("Die Helligkeit muss zwischen 0 und 100 Prozent liegen");
            return false;
        }
         return true;
    }
}
