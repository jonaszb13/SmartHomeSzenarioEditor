package data.models.fachobjekte;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Szenario extends DAO {
    private String beschreibung;
    private Timestamp naesteAusfuerung;
    private String rythmus;
    private final Map<Integer,Aenderungen> aenderungen;

    public Szenario(final UUID id, final String name) {
        super(id, name);
        this.aenderungen = new HashMap<>();
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

    public Map<Integer,Aenderungen> getAenderungen() {
        return aenderungen;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(final String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public record Aenderungen(Geraet geraet, String attribut, String value) {
    }
}
