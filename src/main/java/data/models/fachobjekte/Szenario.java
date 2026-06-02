package data.models.fachobjekte;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Szenario extends DAO {
    private boolean status;
    private String beschreibung;
    private Timestamp naesteAusfuerung;
    private String rythmus;
    private final Map<Integer, Aenderung> aenderungen;

    public Szenario(final UUID id, final String name) {
        super(id, name);
        this.aenderungen = new HashMap<>();
    }

    public boolean isActive() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getRythmus() {
        return rythmus;
    }

    public void setRythmus(final String rythmus) {
        this.rythmus = rythmus;
    }

    public Timestamp getNaesteAusfuerung() {
        return naesteAusfuerung;
    }

    public void setNaesteAusfuerung(final Timestamp naesteAusfuerung) {
        this.naesteAusfuerung = naesteAusfuerung;
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

    //TODO Checken, ob das nur von den richtigen stellen aufgerufen wird
    public record Aenderung(UUID id, Geraet geraet, String beschreibung, String schluessel, String wert) {
    }
}
