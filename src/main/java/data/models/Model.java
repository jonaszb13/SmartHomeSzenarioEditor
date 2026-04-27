package data.models;

import data.models.ansichten.Uebersicht;

import java.util.List;

public class Model {
        private final Uebersicht uebersicht;

        public Model(List<Raum> raeume, List<Geraet> geraete, List<Szenario> szenarien) {
            this.uebersicht = new Uebersicht(raeume, geraete, szenarien);
        }
}
