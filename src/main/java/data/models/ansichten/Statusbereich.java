package data.models.ansichten;

import util.DebugLog;
import util.Meldung;

import java.util.List;

public class Statusbereich {


    //TODO möglicherweise umstieg von StringBuilder
    public String getNachrichten() {
        StringBuilder meldungsAusgabe = new StringBuilder();
        List<Meldung> meldungen = DebugLog.getInstance().getDebugLogEintraege().reversed();
        for (Meldung meldung : meldungen) {
            meldungsAusgabe.append(meldung.getMeldungsTyp()).append(": ").append(meldung.getMeldungstext()).append("\n");
        }
        return meldungsAusgabe.toString();
    }
}
