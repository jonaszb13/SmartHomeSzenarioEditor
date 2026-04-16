package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

import java.awt.*;
import java.util.HashMap;

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
    public void setValues(HashMap<String, String> hashMap) throws IllegalArgumentException {
        String haelligkeit = hashMap.get("haelligkeit");
        String farbe = hashMap.get("farbe");
        String eingeschaltet = hashMap.get("eingeschaltet");
        if (haelligkeit != null) this.haelligkeit = Double.parseDouble(haelligkeit);
        if (farbe != null) this.farbe = Color.decode(farbe);
        if (eingeschaltet != null) this.eingeschaltet = Boolean.parseBoolean(eingeschaltet);
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
