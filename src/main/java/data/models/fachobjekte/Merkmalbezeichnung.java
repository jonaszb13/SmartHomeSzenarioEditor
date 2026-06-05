package data.models.fachobjekte;

public enum Merkmalbezeichnung {
    ZIELTEMP("Zieltemperatur"),
    HELLIGKEIT("Helligkeit"),
    FARBE("Farbe"),
    EINGESCHALTET("Eingeschaltet"),
    STAERKE("Stärke"),
    SCHLIESSSTATUS("Schließstatus"),
    NEIGUNG("Neigung"),
    AUSSCHLAG("Ausschlag"),
    AKTUELLE_LEISTUNG("Aktuelle Leistung");

    private final String bezeichnung;

    Merkmalbezeichnung(final String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }
}
