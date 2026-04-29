package data.models;

import data.models.ansichten.Statusbereich;
import data.models.ansichten.Uebersicht;
import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.Szenario;

import java.util.List;

public class Model {
        private final Uebersicht uebersicht;
        private final Statusbereich statusbereich;

        public Model(List<Raum> raeume, List<Geraet> geraete, List<Szenario> szenarien) {
            this.uebersicht = new Uebersicht(raeume, geraete, szenarien);
            this.statusbereich = new Statusbereich();
        }

        public Uebersicht getUebersicht() {
            return uebersicht;
        }

        public Statusbereich getStatusbereich() {
            return statusbereich;
        }
}
