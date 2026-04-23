package data.daos;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Szenario extends DAO {
    String beschreibung;
    Timestamp naesteAusfuerung;
    String rythmus;
    Map<Integer,Aenderungen> aenderungen;

    public Szenario(int id, String name) {
        super(id, name);
        this.aenderungen = new HashMap<Integer, Aenderungen>();
    }

    public String getRythmus() {
        return rythmus;
    }

    public void setRythmus(String rythmus) {
        this.rythmus = rythmus;
    }

    public Timestamp getNaesteAusfuerung() {
        return naesteAusfuerung;
    }

    public void setNaesteAusfuerung(Timestamp naesteAusfuerung) {
        this.naesteAusfuerung = naesteAusfuerung;
    }

    public Map<Integer,Aenderungen> getAenderungen() {
        return aenderungen;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public record Aenderungen(Geraet geraet, String attribut, String value) {
    }
}
