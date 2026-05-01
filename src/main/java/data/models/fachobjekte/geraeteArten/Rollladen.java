package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;

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
