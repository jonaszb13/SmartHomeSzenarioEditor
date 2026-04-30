package data.models.fachobjekte;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;

public class Szenario extends DAO {
    private String beschreibung;
    private Timestamp naesteAusfuerung;
    private String rythmus;
    private final HashMap<Integer, Aenderung> aenderungen;

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

    public HashMap<Integer, Aenderung> getAenderungen() {
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
