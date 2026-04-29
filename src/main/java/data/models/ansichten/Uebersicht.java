package data.models.ansichten;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;

import java.util.List;

public class Uebersicht {
    private final List<Raum> raeume;
    private final List<Geraet> geraete;
    private final List<Szenario> szenarien;

    public Uebersicht(List<Raum> raeume, List<Geraet> geraete, List<Szenario> szenarien) {
        this.raeume = raeume;
        this.geraete = geraete;
        this.szenarien = szenarien;
    }
}
