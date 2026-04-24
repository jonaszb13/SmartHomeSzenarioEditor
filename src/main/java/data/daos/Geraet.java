package data.daos;

import java.util.Map;



public abstract class Geraet extends DAO {
    private Raum raum;

    public Geraet(int id, String name, Raum raum) {
        super(id, name);
        this.raum = raum;
    }

    public Raum getRaum() {
        return raum;
    }

    public void setRaum(Raum raum) {
        this.raum = raum;
    }

    public void setValues(Map<String, String> map) throws IllegalArgumentException {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            updateValue(entry.getKey(), entry.getValue());
        }
    }

    public abstract void updateValue(String key, String value) throws IllegalArgumentException;

}
