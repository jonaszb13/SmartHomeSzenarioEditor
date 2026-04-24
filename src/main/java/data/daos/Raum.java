package data.daos;

import java.util.ArrayList;
import java.util.List;

public class Raum extends DAO {
    private List<Geraet> geraete;

    public Raum(int id, String name) {
        super(id, name);
        geraete = new ArrayList<>();
    }

    public List<Geraet> getGeraete() {
        return geraete;
    }

}
