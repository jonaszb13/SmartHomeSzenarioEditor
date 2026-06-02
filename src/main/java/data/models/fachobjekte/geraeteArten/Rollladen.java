package data.models.fachobjekte.geraeteArten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Rollladen extends Geraet {

    private float schliessstatus;
    private float winkelung;

    public Rollladen(final UUID id, final String name, final Raum raum) {
        super(id, name, raum);
    }

    public float getSchliessstatus() {
        return schliessstatus;
    }

    public void setSchliessstatus(final float schliessstatus) {
        this.schliessstatus = schliessstatus;
    }

    public float getWinkelung() {
        return winkelung;
    }

    public void setWinkelung(final float winkelung) {
        this.winkelung = winkelung;
    }

    @Override
    public void updateValue(final String key, final String value) {
        switch (key) {
            case "schliessstatus":
                setSchliessstatus(Float.parseFloat(value));
                break;
            case "winkelung":
                setWinkelung(Float.parseFloat(value));
                break;
            default:
                throw new IllegalArgumentException("Ungültiger Schlüssel in der Datenbank");
        }
    }

    @Override
    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        values.put("schliessstatus", Float.toString(getSchliessstatus()));
        values.put("winkelung", Float.toString(getWinkelung()));
        return values;
    }

    @Override
    public Map<String, Class<?>> getAttributTypen() {
        final Map<String, Class<?>> typen = new HashMap<>();
        typen.put("schliessstatus", float.class);
        typen.put("winkelung", float.class);
        return typen;
    }
}
