package controller;

import data.models.fachobjekte.Szenario;

import java.util.ArrayList;
import java.util.List;

class SzenarioFormState {

    Szenario szenarioImBearbeitungsmodus = null;
    String name = "";
    String beschreibung = "";
    final List<Szenario.Aenderung> aktionen = new ArrayList<>();
    Integer editAktionIndex = null;

    void setze(final String name, final String beschreibung, final List<Szenario.Aenderung> aktionen) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.aktionen.clear();
        this.aktionen.addAll(aktionen);
    }

    void zuruecksetzen() {
        szenarioImBearbeitungsmodus = null;
        name = "";
        beschreibung = "";
        aktionen.clear();
        editAktionIndex = null;
    }
}
