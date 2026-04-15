package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

public class Steckdose extends Geraet {
    private boolean strom;
    public Steckdose(int id, String name, Raum raum) {
        super(id, name, raum);
        strom = false;
    }

    public Steckdose(int id, String name, Raum raum, boolean strom) {
        super(id, name, raum);
        this.strom = strom;
    }

    public boolean isStrom() {
        return strom;
    }

    public void setStrom(boolean strom) {
        this.strom = strom;
    }
}
