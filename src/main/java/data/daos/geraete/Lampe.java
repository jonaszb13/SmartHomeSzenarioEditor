package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

import java.awt.*;
import java.util.Map;

public class Lampe extends Geraet {
    private double haelligkeit;
    private Color farbe;
    private boolean eingeschaltet;

    public Lampe(int id, String name, Raum raum, double haelligkeit, Color farbe, boolean eingeschaltet) {
        super(id, name, raum);
        this.haelligkeit = haelligkeit;
        this.farbe = farbe;
        this.eingeschaltet = eingeschaltet;
    }
    public Lampe(int id, String name, Raum raum) {
        super(id, name, raum);
    }

    @Override
    public void setValues(Map<String, String> map) throws IllegalArgumentException {
        final String haelligkeit = map.get("haelligkeit");
        final String farbe = map.get("farbe");
        final String eingeschaltet = map.get("eingeschaltet");
        if (haelligkeit != null) this.haelligkeit = Double.parseDouble(haelligkeit);
        else throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        if (farbe != null) this.farbe = Color.decode(farbe);
        else throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        if (eingeschaltet != null) this.eingeschaltet = Boolean.parseBoolean(eingeschaltet);
        else throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
    }

    public double getHaelligkeit() {
        return haelligkeit;
    }

    public void setHaelligkeit(double haelligkeit) {
        this.haelligkeit = haelligkeit;
    }

    public Color getFarbe() {
        return farbe;
    }

    public void setFarbe(Color farbe) {
        this.farbe = farbe;
    }

    public boolean isEingeschaltet() {
        return eingeschaltet;
    }

    public void setEingeschaltet(boolean eingeschaltet) {
        this.eingeschaltet = eingeschaltet;
    }
}
