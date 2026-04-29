package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;

import java.awt.*;
import java.util.UUID;

public class Lampe extends Geraet {
    private double haelligkeit;
    private Color farbe;
    private boolean eingeschaltet;

    public Lampe(final UUID id, final String name, final Raum raum, final double haelligkeit, final Color farbe, final boolean eingeschaltet) {
        super(id, name, raum);
        this.haelligkeit = haelligkeit;
        this.farbe = farbe;
        this.eingeschaltet = eingeschaltet;
    }
    public Lampe(final UUID id, final String name, final Raum raum) {
        super(id, name, raum);
    }

    public double getHaelligkeit() {
        return haelligkeit;
    }

    public void setHaelligkeit(final double haelligkeit) {
        this.haelligkeit = haelligkeit;
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
            case "haelligkeit":
                setHaelligkeit(Double.parseDouble(value));
                break;
            case "farbe":
                setFarbe(Color.decode(value));
                break;
            default:
                throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }
}
