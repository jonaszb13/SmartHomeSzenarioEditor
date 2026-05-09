package data.models.fachobjekte;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public abstract class Geraet extends DAO {
    private Raum raum;

    public Geraet(final UUID id, final String name, final Raum raum) {
        super(id, name);
        this.raum = raum;
    }

    public Raum getRaum() {
        return raum;
    }

    public void setRaum(final Raum raum) {
        this.raum = raum;
    }

    public void setValues(final Map<String, String> map) {
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            updateValue(entry.getKey(), entry.getValue());
        }
    }

    public abstract void updateValue(String key, String value);

}
