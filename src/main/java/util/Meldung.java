package util;

import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Stack;

public class Meldung {

    private Meldungstyp meldungstyp;

    private String meldungstext;

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
        return Meldungstyp.FEHLER.equals(meldungstyp);
    }

    public String getMeldungstext() {
        return meldungstext;
    }

    public StackTraceElement[] getStackTrace() {
        return exceptionForDebugLog.getStackTrace();
    }
}
