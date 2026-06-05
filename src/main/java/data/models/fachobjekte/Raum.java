package data.models.fachobjekte;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Raum extends DataAccessObject {
    private final List<Geraet> geraete;

    public Raum(final UUID id, final String name) {
        super(id, name);
        geraete = new ArrayList<>();
    }

    public List<Geraet> getGeraete() {
        return geraete;
    }

}
