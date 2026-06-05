package data.services.objekt_services;

import data.models.fachobjekte.Geraet;
import data.models.fachobjekte.Szenario;
import data.services.daten_services.SzenarioDataService;
import jakarta.inject.Singleton;
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
    public static final String WURDE_ERFOLGREICH_AKTUALISIERT = " wurde erfolgreich aktualisiert.";
    public static final String KONNTE_NICHT_AKTUALISIERT_WERDEN = " konnte nicht aktualisiert werden.";

    private SzenarioObjektService(final SzenarioDataService szenarioDataService) {
        this.szenarioDataService = szenarioDataService;
    }

    public static SzenarioObjektService getInstance() throws SQLException {
        if (instance == null) {
            instance = new SzenarioObjektService(SzenarioDataService.getInstance());
        }
        return instance;
    }

    public Map<UUID, Szenario> getSzenarioMap() {
        return szenarioMap;
    }

    public Szenario.Aenderung getAenderung(final Geraet geraet, final String beschreibung, final String schluessel, final String wert) {
        return new Szenario.Aenderung(UUID.randomUUID(), geraet, beschreibung, schluessel, wert);
    }

    public void ladeAlleSzenarien(final Map<UUID, Geraet> geraetMap) throws SQLException {
        StatusLog.addHinweis("Beginne SzenarienMap zu laden");
        final Map<UUID, Szenario> localSzenarienMap = new HashMap<>();
        final CachedRowSet crs = szenarioDataService.getAllGeraete();
        UUID lastId = null;
        Szenario aktuellesSzenario = null;
        while (crs.next()) {
            final UUID id = UUID.fromString(crs.getString(1));
            //Beim ersten Szenario und jedem neuen Gerät Wahr
            if (!id.equals(lastId)) {
                final String name = crs.getString("name");
                aktuellesSzenario = new Szenario(id, name);
                aktuellesSzenario.setBeschreibung(crs.getString("beschreibung"));
                localSzenarienMap.put(id, aktuellesSzenario);
                lastId = id;
            }
            final Geraet geraet = geraetMap.get(UUID.fromString(crs.getString("geraet")));
            if (geraet == null) {
                StatusLog.addError("Szenario-Aktion verweist auf unbekanntes Gerät, Aktion wird übersprungen");
            } else {
                aktuellesSzenario.getAenderungen().put(crs.getInt("position"), new Szenario.Aenderung(
                        UUID.fromString(crs.getString(9)),
                        geraet,
                        crs.getString("Aktion"),
                        crs.getString("schluessel"),
                        crs.getString("wert")
                ));
            }
        }
        StatusLog.addHinweis("SzenarienMap erfolgreich geladen");
        szenarioMap = localSzenarienMap;
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
            StatusLog.addHinweis(szenario.getName() + " wurde erfolgreich erstellt.");
            erfolgreich = true;
        } else {
            StatusLog.addError(szenario.getName() + " konnte nicht erstellt werden.");
        }
        return erfolgreich;
    }

    public boolean updateSzenario(final Szenario szenario, final String name, final String beschreibung) {
        boolean erfolgreich = false;
        szenario.setName(name);
        szenario.setBeschreibung(beschreibung);
        if (szenarioDataService.updateSzenario(szenario)) {
            StatusLog.addHinweis(szenario.getName() + WURDE_ERFOLGREICH_AKTUALISIERT);
            erfolgreich = true;
        } else {
            StatusLog.addError(szenario.getName() + KONNTE_NICHT_AKTUALISIERT_WERDEN);
        }
        return erfolgreich;
    }

    public boolean updateSzenarioName(final Szenario szenario, final String name) {
        boolean erfolgreich = false;
        szenario.setName(name);
        if (szenarioDataService.updateSzenario(szenario)) {
            StatusLog.addHinweis(szenario.getName() + WURDE_ERFOLGREICH_AKTUALISIERT);
            erfolgreich = true;
        } else {
            StatusLog.addError(szenario.getName() + KONNTE_NICHT_AKTUALISIERT_WERDEN);
        }
        return erfolgreich;
    }

    public boolean updateSzenarioBeschreibung(final Szenario szenario, final String beschreibung) {
        boolean erfolgreich = false;
        szenario.setBeschreibung(beschreibung);
        if (szenarioDataService.updateSzenario(szenario)) {
            StatusLog.addHinweis(szenario.getName() + WURDE_ERFOLGREICH_AKTUALISIERT);
            erfolgreich = true;
        } else {
            StatusLog.addError(szenario.getName() + KONNTE_NICHT_AKTUALISIERT_WERDEN);
        }
        return erfolgreich;
    }

    public boolean deleteSzenario(final Szenario szenario) {
        boolean erfolgreich = false;
        if (szenarioDataService.deleteSzenario(szenario)) {
            szenarioMap.remove(szenario.getId());
            StatusLog.addHinweis(szenario.getName() + " wurde erfolgreich gelöscht.");
            erfolgreich = true;
        } else {
            StatusLog.addError(szenario.getName() + " konnte nicht gelöscht werden.");
        }
        return erfolgreich;
    }

    public boolean addSzenarioInhalt(final Szenario szenario, final Szenario.Aenderung aenderung, final int position) {
        boolean erfolgreich = false;
        if (szenarioDataService.addSzenarioInhalt(szenario, aenderung, position)) {
            szenario.getAenderungen().put(position, aenderung);
            StatusLog.addHinweis("Dem Szenario: " + szenario.getName() + " wurde eine Aktion erfolgreich hinzugefügt.");
            erfolgreich = true;
        } else {
            StatusLog.addError("Dem Szenario: " + szenario.getName() + " konnte keine Aktion hinzugefügt werden.");
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
            StatusLog.addHinweis("Änderung im Szenario: " + szenario.getName() + WURDE_ERFOLGREICH_AKTUALISIERT);
            erfolgreich = true;
        } else {
            StatusLog.addError("Änderung im Szenario: " + szenario.getName() + KONNTE_NICHT_AKTUALISIERT_WERDEN);
        }
        return erfolgreich;
    }

    public boolean deleteSzenarioInhalt(final Szenario szenario, final int position) {
        boolean erfolgreich = false;
        if (szenarioDataService.deleteSzenarioInhalt(szenario.getAenderungen().get(position).id())) {
            szenario.getAenderungen().remove(position);
            StatusLog.addHinweis("Änderung aus Szenario: " + szenario.getName() + " erfolgreich gelöscht");
            erfolgreich = true;
        } else {
            StatusLog.addError("Änderung aus Szenario: " + szenario.getName() + " konnte nicht gelöscht werden");
        }
        return erfolgreich;
    }
}
