package data.services.objektServices;

import data.models.fachobjekte.Szenario;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public final class SzenarioAusfuehrungsService {
    private static SzenarioAusfuehrungsService instance;
    private final GeraetObjektService geraetObjektService;

    private SzenarioAusfuehrungsService(final GeraetObjektService geraetObjektService) {
        this.geraetObjektService = geraetObjektService;
    }

    public static SzenarioAusfuehrungsService getInstance() throws SQLException {
        if (instance == null) {
            instance = new SzenarioAusfuehrungsService(GeraetObjektService.getInstance());
        }
        return instance;
    }

    public boolean fuehreSzenarioAus(final Szenario szenario) {
        boolean erfolgreich = true;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM 'um' HH:mm:ss 'Uhr'", Locale.GERMANY);
        for (Szenario.Aenderung aenderung : szenario.getAenderungen().values()) {
            Map<String, String> attributeMap = aenderung.geraet().getValues();
            attributeMap.replace(aenderung.schluessel(), aenderung.wert());
            if (!aenderung.geraet().isGueltigeAttribute(attributeMap)) {
                StatusLog.addError("Attribute des Geräts " + aenderung.geraet().getName() + " können nicht aktualisiert werden");
                erfolgreich = false;
                break;
            }
        }
        if (erfolgreich) {
            for (Szenario.Aenderung aenderung : szenario.getAenderungen().values()) {
                Map<String, String> attributeMap = aenderung.geraet().getValues();
                attributeMap.replace(aenderung.schluessel(), aenderung.wert());
                geraetObjektService.updateGeraetWerte(aenderung.geraet(), attributeMap);
            }
            StatusLog.addHinweis("Szenario " + szenario.getName() + " erfolgreich am " + sdf.format(new Date()) + " ausgeführt");
        } else {
            StatusLog.addError("Szenario " + szenario.getName() + " konnte nicht vollständig ausgeführt werden");
        }
        return erfolgreich;
    }
}
