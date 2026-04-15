package data.daos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Szenario extends DAO {
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

    private record aenderungen(Geraet geraet, String attribut, String value) {
    }
}
