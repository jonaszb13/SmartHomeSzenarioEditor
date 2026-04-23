package data.daos;

import java.util.Map;

public abstract class Geraet extends DAO {
    public Geraet(int id, String name, Raum raum) {
        super(id, name);
        this.raum = raum;
    }

    Raum raum;

    public Raum getRaum() {
        return raum;
    }

    public void setRaum(Raum raum) {
        this.raum = raum;
    }

    public abstract void setValues(Map<String, String> map) throws IllegalArgumentException;

}
