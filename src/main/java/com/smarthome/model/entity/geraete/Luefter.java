package com.smarthome.model.entity.geraete;

import com.smarthome.model.entity.Geraet;
import com.smarthome.model.entity.Raum;

import java.util.UUID;

public class Luefter extends Geraet {
    private boolean eingeschaltet;
    private float staerke;

    public Luefter(final UUID id, final String name, final Raum raum) {
        super(id, name, raum);
    }

    public boolean isEingeschaltet() {
        return eingeschaltet;
    }

    public void setEingeschaltet(final boolean eingeschaltet) {
        this.eingeschaltet = eingeschaltet;
    }

    public float getStaerke() {
        return staerke;
    }

    public void setStaerke(final float staerke) {
        this.staerke = staerke;
    }

    @Override
    public void updateValue(final String key, final String value) {
        switch (key) {
            case "eingeschaltet" -> setEingeschaltet(Boolean.parseBoolean(value));
            case "staerke"       -> setStaerke(Float.parseFloat(value));
            default -> throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }
}
