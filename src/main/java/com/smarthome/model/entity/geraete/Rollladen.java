package com.smarthome.model.entity.geraete;

import com.smarthome.model.entity.Geraet;
import com.smarthome.model.entity.Raum;

import java.util.UUID;

public class Rollladen extends Geraet {
    private float schliessstatus;
    private float winckelung;

    public Rollladen(final UUID id, final String name, final Raum raum) {
        super(id, name, raum);
    }

    public float getSchliessstatus() {
        return schliessstatus;
    }

    public void setSchliessstatus(final float schliessstatus) {
        this.schliessstatus = schliessstatus;
    }

    public float getWinckelung() {
        return winckelung;
    }

    public void setWinckelung(final float winckelung) {
        this.winckelung = winckelung;
    }

    @Override
    public void updateValue(final String key, final String value) {
        switch (key) {
            case "schliessstatus" -> setSchliessstatus(Float.parseFloat(value));
            case "winckelung"     -> setWinckelung(Float.parseFloat(value));
            default -> throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }
}
