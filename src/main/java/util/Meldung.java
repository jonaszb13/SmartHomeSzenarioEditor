package util;

public class Meldung {

    private final Meldungstyp meldungstyp;

    private final String meldungstext;

    private Throwable exceptionForDebugLog;

    public Meldung(Meldungstyp meldungstyp, String meldungstext) {
        this.meldungstyp = meldungstyp;
        this.meldungstext = meldungstext;
    }

    public Meldung(Meldungstyp meldungstyp, String meldungstext, Throwable exception) {
        this.meldungstyp = meldungstyp;
        this.meldungstext = meldungstext;
        this.exceptionForDebugLog = exception;
    }

    public boolean isError() {
        return Meldungstyp.FEHLER == meldungstyp;
    }

    public String getMeldungsTyp() {
        return meldungstyp.getBezeichnung();
    }

    public String getMeldungstext() {
        return meldungstext;
    }

    public StackTraceElement[] getStackTrace() {
        return exceptionForDebugLog == null ? null : exceptionForDebugLog.getStackTrace();
    }
}
