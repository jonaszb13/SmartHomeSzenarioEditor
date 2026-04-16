package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

import java.util.HashMap;

public class Rollladen extends Geraet {
    public Rollladen(int id, String name, Raum raum) {
        super(id, name, raum);
    }

    @Override
    public void setValues(HashMap<String, String> hashMap) throws IllegalArgumentException {

    }

}
