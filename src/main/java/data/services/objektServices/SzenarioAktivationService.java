package data.services.objektServices;

import data.models.fachobjekte.Szenario;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM 'um' HH:mm:ss 'Uhr'", Locale.GERMANY);
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
            StatusLog.addHinweis("Szenario " + szenario.getName() + " erfolgreich am " + sdf.format(new Date()) +" ausgeführt");
        } else {
            StatusLog.addError("Szenario " + szenario.getName() + " konnte nicht aktiviert werden");
        }
        return erfolgreich;
    }

    public boolean deaktiviereSzenario(final Szenario szenario) {
        return szenarioObjektService.updateSzenarioStatus(szenario, false);
    }
}
