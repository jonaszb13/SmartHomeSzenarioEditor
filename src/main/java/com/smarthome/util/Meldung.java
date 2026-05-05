package com.smarthome.util;

import java.util.UUID;

public class Meldung {

    private final UUID meldungsId;
    private final Meldungstyp meldungstyp;
    private final String meldungstext;
    private Throwable exceptionForDebugLog;

    public Meldung(final Meldungstyp meldungstyp, final String meldungstext) {
        meldungsId = UUID.randomUUID();
        this.meldungstyp = meldungstyp;
        this.meldungstext = meldungstext;
    }

    public Meldung(final Meldungstyp meldungstyp, final String meldungstext, final Throwable exception) {
        meldungsId = UUID.randomUUID();
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

    public UUID getMeldungsId() {
        return meldungsId;
    }
}