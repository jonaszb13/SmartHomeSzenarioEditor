package data.models.fachobjekte;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Szenario extends DataAccessObject {
    private String beschreibung;
    private final Map<Integer, Aenderung> aenderungen;

    public Szenario(final UUID id, final String name) {
        super(id, name);
        this.aenderungen = new HashMap<>();
    }

    public Map<Integer, Aenderung> getAenderungen() {
        return aenderungen;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(final String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public record Aenderung(UUID id, Geraet geraet, String beschreibung, String schluessel, String wert) {
    }
}
