package data.services.gvk;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Szenario;
import data.services.datenServices.DataAccess;
import data.services.datenServices.SzenarioDataService;
import jakarta.inject.Singleton;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public final class SzenarioObjektService {
    private static SzenarioObjektService instance;
    private final SzenarioDataService szenarioDataService;
    private final Map<UUID, Szenario> szenarioMap;
    private final String szenarioString = "Szenario ";

    private SzenarioObjektService(SzenarioDataService szenarioDataService, Map<UUID, Szenario> szenarioMap) {
        this.szenarioDataService = szenarioDataService;
        this.szenarioMap = szenarioMap;
    }

    public static SzenarioObjektService getInstance() throws SQLException {
        if (instance == null) {
            DataAccess dataAccess = DataAccess.getInstance();
            instance = new SzenarioObjektService(SzenarioDataService.getInstance(dataAccess), new HashMap<>());
            dataAccess.mapAllSzenarien(GeraetObjektService.getInstance().getGeraetMap(), instance.szenarioMap);
        }
        return instance;
    }

    public Map<UUID, Szenario> getSzenarioMap() {
        return szenarioMap;
    }

    public Szenario.Aenderung getAenderung(Geraet geraet, String beschreibung, String schluessel, String wert) {
        return new Szenario.Aenderung(UUID.randomUUID(), geraet, beschreibung, schluessel, wert);
    }

    public boolean addSzenario(String name, String beschreibung, Map<Integer, Szenario.Aenderung> aenderung) {
        boolean erfolgreich = false;
        UUID uuid = UUID.randomUUID();
        while (szenarioMap.containsKey(uuid)) {
            uuid = UUID.randomUUID();
        }
        Szenario szenario = new Szenario(uuid, name);
        szenario.setBeschreibung(beschreibung);
        for (Map.Entry<Integer, Szenario.Aenderung> entry : aenderung.entrySet()) {
            szenario.getAenderungen().put(entry.getKey(), entry.getValue());
        }
        if (szenarioDataService.addSzenario(szenario)) {
            szenarioMap.put(uuid, szenario);
            StatusLog.addHinweis(szenarioString + szenario.getId() + " wurde erfolgreich erstellt.");
            erfolgreich = true;
        } else {
            StatusLog.addError(szenarioString + szenario.getId() + " konnte nicht erstellt werden.");
        }
        return erfolgreich;
    }

    public boolean updateSzenario(Szenario szenario, String name, String beschreibung) {
        boolean erfolgreich = false;
        szenario.setName(name);
        szenario.setBeschreibung(beschreibung);
        if (szenarioDataService.updateSzenario(szenario)) {
            StatusLog.addHinweis(szenarioString + szenario.getId() + " wurde erfolgreich aktualisiert.");
            erfolgreich = true;
        } else {
            StatusLog.addError(szenarioString + szenario.getId() + " konnte nicht aktualisiert werden.");
        }
        return erfolgreich;
    }

    public boolean updateSzenarioName(Szenario szenario, String name) {
        boolean erfolgreich = false;
        szenario.setName(name);
        if (szenarioDataService.updateSzenario(szenario)) {
            StatusLog.addHinweis(szenarioString + szenario.getId() + " wurde erfolgreich aktualisiert.");
            erfolgreich = true;
        } else {
            StatusLog.addError(szenarioString + szenario.getId() + " konnte nicht aktualisiert werden.");
        }
        return erfolgreich;
    }

    public boolean updateSzenarioBeschreibung(Szenario szenario, String beschreibung) {
        boolean erfolgreich = false;
        szenario.setBeschreibung(beschreibung);
        if (szenarioDataService.updateSzenario(szenario)) {
            StatusLog.addHinweis(szenarioString + szenario.getId() + " wurde erfolgreich aktualisiert.");
            erfolgreich = true;
        } else {
            StatusLog.addError(szenarioString + szenario.getId() + " konnte nicht aktualisiert werden.");
        }
        return erfolgreich;
    }

    public boolean deleteSzenario(Szenario szenario) {
        boolean erfolgreich = false;
        if (szenarioDataService.deleteSzenario(szenario)) {
            szenarioMap.remove(szenario.getId());
            StatusLog.addHinweis(szenarioString + szenario.getId() + " wurde erfolgreich gelöscht.");
            erfolgreich = true;
        } else {
            StatusLog.addError(szenarioString + szenario.getId() + " konnte nicht gelöscht werden.");
        }
        return erfolgreich;
    }

    public boolean addSzenarioInhalt(Szenario szenario, Szenario.Aenderung aenderung, int position) {
        boolean erfolgreich = false;
        if (szenarioDataService.addSzenarioInhalt(szenario, aenderung, position)) {
            szenario.getAenderungen().put(position, aenderung);
            StatusLog.addHinweis("Dem Szenario " + szenario.getId() + " wurde eine Aktion erfolgreich hinzugefügt.");
            erfolgreich = true;
        } else {
            StatusLog.addError("Dem Szenario " + szenario.getId() + " konnte keine Aktion hinzugefügt werden.");
        }
        return erfolgreich;
    }

    public boolean alterSzenarioInhalt(Szenario szenario, int position, Geraet geraet, String beschreibung, String schluessel, String wert) {
        boolean erfolgreich = false;
        Szenario.Aenderung neueAenderung = new Szenario.Aenderung(szenario.getAenderungen().get(position).id(),
                geraet, beschreibung, schluessel, wert);
        if (szenarioDataService.alterSzenarioInhalt(neueAenderung, position)) {
            szenario.getAenderungen().replace(position, neueAenderung);
            StatusLog.addHinweis("Änderung im Szenario " + szenario.getId() + " erfolgreich geändert");
            erfolgreich = true;
        } else {
            StatusLog.addError("Änderung im Szenario " + szenario.getId() + " konnte nicht geändert werden.");
        }
        return erfolgreich;
    }

    public boolean deleteSzenarioInhalt(Szenario szenario, int position) {
        boolean erfolgreich = false;
        if (szenarioDataService.deleteSzenarioInhalt(szenario.getAenderungen().get(position).id())) {
            szenario.getAenderungen().remove(position);
            StatusLog.addHinweis("Änderung aus Szenario " + szenario.getId() + " erfolgreich gelöscht");
            erfolgreich = true;
        } else {
            StatusLog.addError("Änderung aus Szenario " + szenario.getId() + " konnte nicht gelöscht werden");
        }
        return erfolgreich;
    }


}
