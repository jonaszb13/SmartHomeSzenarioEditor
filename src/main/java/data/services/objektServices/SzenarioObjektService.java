package data.services.objektServices;

import data.models.Model;
import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Szenario;
import data.services.datenServices.SzenarioDataService;
import jakarta.inject.Singleton;
import javafx.scene.control.TreeItem;
import util.DoubleMap;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Singleton
public final class SzenarioObjektService {
    private static SzenarioObjektService instance;
    private final SzenarioDataService szenarioDataService;
    private final Map<UUID, Szenario> szenarioMap;
    private final DoubleMap<UUID, TreeItem<String>> szenarioTreeMap;
    private static final String SZENARIO_STRING = "Szenario ";

    private SzenarioObjektService(final SzenarioDataService szenarioDataService, final Map<UUID, Szenario> szenarioMap, final DoubleMap<UUID, TreeItem<String>> szenarioTreeMap) {
        this.szenarioDataService = szenarioDataService;
        this.szenarioMap = szenarioMap;
        this.szenarioTreeMap = szenarioTreeMap;
    }

    public static SzenarioObjektService getInstance() throws SQLException {
        if (instance == null) {
            Model model = Model.getInstance();
            instance = new SzenarioObjektService(SzenarioDataService.getInstance(), model.getDaten().szenarioMap(), model.getUebersicht().szenarioTreeMap());
        }
        return instance;
    }

    public Map<UUID, Szenario> getSzenarioMap() {
        return szenarioMap;
    }

    public DoubleMap<UUID, TreeItem<String>> getSzenarioTreeMap() {
        return szenarioTreeMap;
    }

    public Szenario.Aenderung getAenderung(final Geraet geraet, final String beschreibung, final String schluessel, final String wert) {
        return new Szenario.Aenderung(UUID.randomUUID(), geraet, beschreibung, schluessel, wert);
    }

    public boolean addSzenario(final String name, final String beschreibung, final Map<Integer, Szenario.Aenderung> aenderung) {
        boolean erfolgreich = false;
        UUID uuid = UUID.randomUUID();
        while (szenarioMap.containsKey(uuid)) {
            uuid = UUID.randomUUID();
        }
        final Szenario szenario = new Szenario(uuid, name);
        szenario.setBeschreibung(beschreibung);
        for (final Map.Entry<Integer, Szenario.Aenderung> entry : aenderung.entrySet()) {
            szenario.getAenderungen().put(entry.getKey(), entry.getValue());
        }
        if (szenarioDataService.addSzenario(szenario)) {
            szenarioMap.put(uuid, szenario);
            szenarioTreeMap.put(uuid, new TreeItem<>(name));
            StatusLog.addHinweis(SZENARIO_STRING + szenario.getId() + " wurde erfolgreich erstellt.");
            erfolgreich = true;
        } else {
            StatusLog.addError(SZENARIO_STRING + szenario.getId() + " konnte nicht erstellt werden.");
        }
        return erfolgreich;
    }

    public boolean updateSzenario(final Szenario szenario, final String name, final String beschreibung) {
        boolean erfolgreich = false;
        szenario.setName(name);
        szenario.setBeschreibung(beschreibung);
        if (szenarioDataService.updateSzenario(szenario)) {
            StatusLog.addHinweis(SZENARIO_STRING + szenario.getId() + " wurde erfolgreich aktualisiert.");
            erfolgreich = true;
        } else {
            StatusLog.addError(SZENARIO_STRING + szenario.getId() + " konnte nicht aktualisiert werden.");
        }
        return erfolgreich;
    }

    public boolean updateSzenarioName(final Szenario szenario, final String name) {
        boolean erfolgreich = false;
        szenario.setName(name);
        if (szenarioDataService.updateSzenario(szenario)) {
            StatusLog.addHinweis(SZENARIO_STRING + szenario.getId() + " wurde erfolgreich aktualisiert.");
            erfolgreich = true;
        } else {
            StatusLog.addError(SZENARIO_STRING + szenario.getId() + " konnte nicht aktualisiert werden.");
        }
        return erfolgreich;
    }

    public boolean updateSzenarioBeschreibung(final Szenario szenario, final String beschreibung) {
        boolean erfolgreich = false;
        szenario.setBeschreibung(beschreibung);
        if (szenarioDataService.updateSzenario(szenario)) {
            StatusLog.addHinweis(SZENARIO_STRING + szenario.getId() + " wurde erfolgreich aktualisiert.");
            erfolgreich = true;
        } else {
            StatusLog.addError(SZENARIO_STRING + szenario.getId() + " konnte nicht aktualisiert werden.");
        }
        return erfolgreich;
    }

    public boolean deleteSzenario(final Szenario szenario) {
        boolean erfolgreich = false;
        if (szenarioDataService.deleteSzenario(szenario)) {
            szenarioMap.remove(szenario.getId());
            szenarioTreeMap.removeByA(szenario.getId());
            StatusLog.addHinweis(SZENARIO_STRING + szenario.getId() + " wurde erfolgreich gelöscht.");
            erfolgreich = true;
        } else {
            StatusLog.addError(SZENARIO_STRING + szenario.getId() + " konnte nicht gelöscht werden.");
        }
        return erfolgreich;
    }

    public boolean updateSzenarioStatus(final Szenario szenario, final boolean status) {
        boolean erfolgreich = false;
        if (szenarioDataService.updateSzenarioStatus(szenario, status)) {
            StatusLog.addHinweis(SZENARIO_STRING + szenario.getId() + " wurde erfolgreich aktualisiert.");
            erfolgreich = true;
        } else {
            StatusLog.addError(SZENARIO_STRING + szenario.getId() + " konnte nicht aktualisiert werden.");
        }
        return erfolgreich;
    }

    public boolean addSzenarioInhalt(final Szenario szenario, final Szenario.Aenderung aenderung, final int position) {
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

    public boolean alterSzenarioInhalt(final Szenario szenario, final int position, final Geraet geraet,
                                       final String beschreibung, final String schluessel, final String wert) {
        boolean erfolgreich = false;
        final Szenario.Aenderung neueAenderung = new Szenario.Aenderung(szenario.getAenderungen().get(position).id(),
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

    public boolean deleteSzenarioInhalt(final Szenario szenario, final int position) {
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
