package data.daos.geraete;

import data.daos.Geraet;
import data.daos.Raum;

import java.util.Map;

public class Luefter extends Geraet {
    public Luefter(int id, String name, Raum raum) {
        super(id, name, raum);
    }

    @Override
    public void setValues(Map<String, String> map) throws IllegalArgumentException {
    }

}
