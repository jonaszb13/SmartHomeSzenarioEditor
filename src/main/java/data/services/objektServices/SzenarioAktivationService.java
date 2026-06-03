package data.services.objektServices;

import data.models.fachobjekte.Szenario;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class SzenarioAktivationService {
    private static SzenarioAktivationService instance;
    private final SzenarioObjektService szenarioObjektService;
    private final GeraetObjektService geraetObjektService;


    private SzenarioAktivationService(final SzenarioObjektService szenarioObjektService, final GeraetObjektService geraetObjektService) {
        this.szenarioObjektService = szenarioObjektService;
        this.geraetObjektService = geraetObjektService;
    }

    public static SzenarioAktivationService getInstance() throws SQLException {
        if (instance == null) {
            instance = new SzenarioAktivationService(SzenarioObjektService.getInstance(), GeraetObjektService.getInstance());
        }
        return instance;
    }

    public boolean aktiviereSzenario(final Szenario szenario) {
        boolean erfolgreich = true;
        for (Szenario.Aenderung aenderung : szenario.getAenderungen().values()) {
            Map<String, String> attributeMap = aenderung.geraet().getValues();
            attributeMap.replace(aenderung.schluessel(), aenderung.wert());
            if (!geraetObjektService.updateGeraetWerte(aenderung.geraet(), attributeMap)) {
                erfolgreich = false;
                break;
            }
        }
        if (erfolgreich && !szenarioObjektService.updateSzenarioStatus(szenario, true)) erfolgreich = false;
        if (erfolgreich) {
            StatusLog.addHinweis("Szenario erfolgreich ausgeführt");
        } else {
            StatusLog.addError("Szenario konnte nicht aktiviert werden");
        }
        return erfolgreich;
    }

    public boolean deaktiviereSzenario(final Szenario szenario) {
        return szenarioObjektService.updateSzenarioStatus(szenario, false);
    }
}
