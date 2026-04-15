package data.daos.geraete;

import data.daos.Geraet;

public class Steckdose extends Geraet {
    private boolean strom;

    public boolean isStrom() {
        return strom;
    }

    public void setStrom(boolean strom) {
        this.strom = strom;
    }
}
