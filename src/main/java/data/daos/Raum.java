package data.daos;

import java.util.ArrayList;
import java.util.List;

public class Raum extends DAO {
    List<Geraet> geraete;

    public Raum(int id, String name) {
        this.id = id;
        this.name = name;
        geraete = new ArrayList<Geraet>();
    }

    public List<Geraet> getGeraete() {
        return geraete;
    }

}
