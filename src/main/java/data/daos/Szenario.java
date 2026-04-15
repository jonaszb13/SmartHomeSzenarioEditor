package data.daos;

import java.sql.Timestamp;
import java.util.List;

public class Szenario extends DAO {
    Timestamp naesteAusfuerung;
    String rythmus;
    List<aenderungen> aenderungen;

    private record aenderungen(Geraet geraet, String attribut, String value) {
    }
}
