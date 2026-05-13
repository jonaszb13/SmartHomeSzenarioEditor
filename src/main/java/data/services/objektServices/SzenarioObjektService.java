package data.services.objektServices;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Szenario;
import data.services.datenServices.SzenarioDataService;
import jakarta.inject.Singleton;
import javafx.scene.control.TreeItem;
import util.DoubleMap;
import util.statusmeldungen.StatusLog;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public final class SzenarioObjektService {
    private static SzenarioObjektService instance;
    private final SzenarioDataService szenarioDataService;
    private Map<UUID, Szenario> szenarioMap;
    private DoubleMap<UUID, TreeItem<String>> szenarioTreeMap;
    private static final String SZENARIO_STRING = "Szenario ";
    public static final String WURDE_ERFOLGREICH_AKTUALISIERT = " wurde erfolgreich aktualisiert.";
    public static final String KONNTE_NICHT_AKTUALISIERT_WERDEN = " konnte nicht aktualisiert werden.";

    private SzenarioObjektService(final SzenarioDataService szenarioDataService, final DoubleMap<UUID, TreeItem<String>> szenarioTreeMap) {
        this.szenarioDataService = szenarioDataService;
        this.szenarioTreeMap = szenarioTreeMap;
    }

    public static SzenarioObjektService getInstance() throws SQLException {
        if (instance == null) {
            instance = new SzenarioObjektService(SzenarioDataService.getInstance(), new DoubleMap<>());
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

    public Map<UUID, Szenario> getAllSzenarien(final Map<UUID, Geraet> geraetMap) throws SQLException {
        StatusLog.addHinweis("Beginne SzenarienMap zu laden");
        Map<UUID, Szenario> localSzenarienMap = new HashMap<>();
        CachedRowSet crs = szenarioDataService.getAllGeraete();
        UUID lastId = null;
        Szenario aktuellesSzenario = null;
        while (crs.next()) {
            final UUID id = UUID.fromString(crs.getString(1));
            //Beim ersten Szenario und jedem neuen Gerät Wahr
            if (!id.equals(lastId)) {
                final String name = crs.getString("name");
                aktuellesSzenario = new Szenario(id, name);
                aktuellesSzenario.setBeschreibung(crs.getString("beschreibung"));
                aktuellesSzenario.setStatus(Boolean.parseBoolean(crs.getString("status")));
                localSzenarienMap.put(id, aktuellesSzenario);
                lastId = id;
            }
            aktuellesSzenario.getAenderungen().put(crs.getInt("position"), new Szenario.Aenderung(
                    UUID.fromString(crs.getString(10)),
                    geraetMap.get(UUID.fromString(crs.getString("geraet"))),
                    crs.getString("Aktion"),
                    crs.getString("schluessel"),
                    crs.getString("wert")
            ));
        }
        StatusLog.addHinweis("SzenarienMap erfolgreich geladen");
        szenarioMap = localSzenarienMap;
        return localSzenarienMap;
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
            StatusLog.addHinweis(SZENARIO_STRING + szenario.getId() + WURDE_ERFOLGREICH_AKTUALISIERT);
            erfolgreich = true;
        } else {
            StatusLog.addError(SZENARIO_STRING + szenario.getId() + KONNTE_NICHT_AKTUALISIERT_WERDEN);
        }
        return erfolgreich;
    }

    public boolean updateSzenarioName(final Szenario szenario, final String name) {
        boolean erfolgreich = false;
        szenario.setName(name);
        if (szenarioDataService.updateSzenario(szenario)) {
            StatusLog.addHinweis(SZENARIO_STRING + szenario.getId() + WURDE_ERFOLGREICH_AKTUALISIERT);
            erfolgreich = true;
        } else {
            StatusLog.addError(SZENARIO_STRING + szenario.getId() + KONNTE_NICHT_AKTUALISIERT_WERDEN);
        }
        return erfolgreich;
    }

    public boolean updateSzenarioBeschreibung(final Szenario szenario, final String beschreibung) {
        boolean erfolgreich = false;
        szenario.setBeschreibung(beschreibung);
        if (szenarioDataService.updateSzenario(szenario)) {
            StatusLog.addHinweis(SZENARIO_STRING + szenario.getId() + WURDE_ERFOLGREICH_AKTUALISIERT);
            erfolgreich = true;
        } else {
            StatusLog.addError(SZENARIO_STRING + szenario.getId() + KONNTE_NICHT_AKTUALISIERT_WERDEN);
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
            StatusLog.addHinweis(SZENARIO_STRING + szenario.getId() + WURDE_ERFOLGREICH_AKTUALISIERT);
            erfolgreich = true;
        } else {
            StatusLog.addError(SZENARIO_STRING + szenario.getId() + KONNTE_NICHT_AKTUALISIERT_WERDEN);
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
            StatusLog.addHinweis("Änderung im Szenario " + szenario.getId() + WURDE_ERFOLGREICH_AKTUALISIERT);
            erfolgreich = true;
        } else {
            StatusLog.addError("Änderung im Szenario " + szenario.getId() + KONNTE_NICHT_AKTUALISIERT_WERDEN);
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
