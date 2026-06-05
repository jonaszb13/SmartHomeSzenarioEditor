package unit.models.fachobjekte.geraetearten;

import data.models.fachobjekte.Merkmalbezeichnung;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.geraetearten.Steckdose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.statusmeldungen.StatusLog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SteckdoseTest {

    private Steckdose steckdose;

    @BeforeEach
    public void createObject() {
        steckdose = new Steckdose(UUID.randomUUID(), "Luefter", new Raum(UUID.randomUUID(), "Raum"), false, 1000);
        StatusLog.clear();
    }

    @Test
    void updateValueGueltigerWert() {
        final String key = Merkmalbezeichnung.EINGESCHALTET.getBezeichnung();
        steckdose.updateValue(key, "true");
        final String key2 = Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung();
        steckdose.updateValue(key2, "1200");
        assertTrue(steckdose.isEingeschaltet());
        assertEquals(1200, steckdose.getAktuelleLeistung());
    }

    @Test
    void updateValueUngueltigerWert() {
        final String key = Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung();
        final String value = "Test";
        assertThrows(NumberFormatException.class, () -> {
            steckdose.updateValue(key, value);
        });
        assertFalse(StatusLog.hasError());
        assertEquals(1000.0, steckdose.getAktuelleLeistung());
        assertEquals("1000,0", steckdose.getValues().get(Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung()));
    }

    @Test
    void updateValueSchluesselNichtVorhanden() {
        final String key = "falsch";
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            steckdose.updateValue(key, "false");
        });
        assertEquals("Ungültiger Schlüssel in der Datenbank", exception.getMessage());
        assertTrue(StatusLog.hasError());
        assertFalse(steckdose.isEingeschaltet());
    }

    @Test
    void isGueltigeAttributeKorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung(), "28,0");
        attributeMap.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "true");
        assertTrue(steckdose.isGueltigeAttribute(attributeMap));
        assertFalse(StatusLog.hasError());
        assertEquals(1000, steckdose.getAktuelleLeistung());
    }

    @Test
    void isGueltigeAttributeTechnischInkorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung(), "achtundzwanzig");
        attributeMap.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "true");
        assertFalse(steckdose.isGueltigeAttribute(attributeMap));
        assertTrue(StatusLog.hasError());
        assertEquals(1000, steckdose.getAktuelleLeistung());
    }

    @Test
    void isGueltigeAttributeFachlichInkorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.AKTUELLE_LEISTUNG.getBezeichnung(), "320000,0");
        attributeMap.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "true");
        assertFalse(steckdose.isGueltigeAttribute(attributeMap));
        assertTrue(StatusLog.hasError());
        assertEquals(1000, steckdose.getAktuelleLeistung());
    }

}
