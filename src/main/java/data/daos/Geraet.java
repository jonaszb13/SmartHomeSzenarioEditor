package data.daos;

import java.util.HashMap;

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

    public abstract void setValues (HashMap<String, String> hashMap) throws IllegalArgumentException;

}
