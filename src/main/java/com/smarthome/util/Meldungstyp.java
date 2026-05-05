package com.smarthome.util;

public enum Meldungstyp {
    FEHLER("Fehler"),
    HINWEIS("Hinweis"),
    METADATEN("Metadaten");

    public final String bezeichnung;

    Meldungstyp(final String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }
}