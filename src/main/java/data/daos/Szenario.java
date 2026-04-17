package data.daos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Szenario extends DAO {
    String beschreibung;
    Timestamp naesteAusfuerung;
    String rythmus;
    List<aenderungen> aenderungen;

    public Szenario(int id, String name) {
        super(id, name);
        this.aenderungen = new ArrayList<aenderungen>();
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

    public List<aenderungen> getAenderungen() {
        return aenderungen;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public record aenderungen(Geraet geraet, String attribut, String value) {
    }
}
