package data.models.ansichten;

import data.models.Geraet;
import data.models.Raum;
import data.models.Szenario;

import java.util.List;

public class Uebersicht {
    private List<Raum> raeume;
    private List<Geraet> geraete;
    private List<Szenario> szenarien;

    public Uebersicht(List<Raum> raeume, List<Geraet> geraete, List<Szenario> szenarien) {
        this.raeume = raeume;
        this.geraete = geraete;
        this.szenarien = szenarien;
    }
}
