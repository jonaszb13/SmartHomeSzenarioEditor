package data;

import java.awt.*;

public class Lampe extends Geraet {
    private double haelligkeit;
    private Color farbe;
    private boolean eingeschaltet;

    public Lampe(int id, double haelligkeit, Color farbe, boolean eingeschaltet) {
        this.id = id;
        this.haelligkeit = haelligkeit;
        this.farbe = farbe;
        this.eingeschaltet = eingeschaltet;
    }
    public Lampe(int id, String name) {
        this.id = id;
        this.name = name;
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
