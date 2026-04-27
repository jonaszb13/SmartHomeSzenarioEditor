package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

import java.util.UUID;

public class Rollladen extends Geraet {

    private float schliessstatus;
    private float winckelung;

    public Rollladen(UUID id, String name, Raum raum) {
        super(id, name, raum);
    }

    public float getSchliessstatus() {
        return schliessstatus;
    }

    public void setSchliessstatus(float schliessstatus) {
        this.schliessstatus = schliessstatus;
    }

    public float getWinckelung() {
        return winckelung;
    }

    public void setWinckelung(float winckelung) {
        this.winckelung = winckelung;
    }

    @Override
    public void updateValue(String key, String value) {
        switch (key) {
            case "schliessstatus":
                setSchliessstatus(Float.parseFloat(value));
                break;
            case "winckelung":
                setWinckelung(Float.parseFloat(value));
                break;
            default:
                throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }
}
