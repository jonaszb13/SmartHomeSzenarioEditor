package unit.models.fachobjekte.geraetearten;

import data.models.fachobjekte.Merkmalbezeichnung;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.geraetearten.Heizung;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.statusmeldungen.StatusLog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class HeizungTest {

    private Heizung heizung;

    @BeforeEach
    public void createObject() {
        heizung = new Heizung(UUID.randomUUID(), "Heizung", new Raum(UUID.randomUUID(), "Raum"), 25.0);
        StatusLog.clear();
    }

    @Test
    void updateValueGueltigerWert() {
        final String key = Merkmalbezeichnung.ZIELTEMP.getBezeichnung();
        final String value = "30.3";
        heizung.updateValue(key, value);
        assertEquals(30.3, heizung.getZielTemp());
        assertEquals("30,3", heizung.getValues().get(Merkmalbezeichnung.ZIELTEMP.getBezeichnung()));
    }

    @Test
    void updateValueUngueltigerWert() {
        final String key = Merkmalbezeichnung.ZIELTEMP.getBezeichnung();
        final String value = "Test";
        assertThrows(NumberFormatException.class, () -> {
            heizung.updateValue(key, value);
        });
        assertFalse(StatusLog.hasError());
        assertEquals(25.0, heizung.getZielTemp());
        assertEquals("25,0", heizung.getValues().get(Merkmalbezeichnung.ZIELTEMP.getBezeichnung()));
    }

    @Test
    void updateValueSchluesselNichtVorhanden() {
        final String key = "falsch";
        final String value = "30.3";
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            heizung.updateValue(key, value);
        });
        assertEquals("Ungültiger Schlüssel in der Datenbank", exception.getMessage());
        assertTrue(StatusLog.hasError());
        assertEquals(25.0, heizung.getZielTemp());
        assertEquals("25,0", heizung.getValues().get(Merkmalbezeichnung.ZIELTEMP.getBezeichnung()));
    }

    @Test
    void isGueltigeAttributeKorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.ZIELTEMP.getBezeichnung(), "28,0");
        assertTrue(heizung.isGueltigeAttribute(attributeMap));
        assertFalse(StatusLog.hasError());
        assertEquals(25.0, heizung.getZielTemp());
    }

    @Test
    void isGueltigeAttributeTechnischInkorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.ZIELTEMP.getBezeichnung(), "zweiunddreißig");
        assertFalse(heizung.isGueltigeAttribute(attributeMap));
        assertTrue(StatusLog.hasError());
        assertEquals(25.0, heizung.getZielTemp());
    }

    @Test
    void isGueltigeAttributeFachlichInkorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.ZIELTEMP.getBezeichnung(), "32,0");
        assertFalse(heizung.isGueltigeAttribute(attributeMap));
        assertTrue(StatusLog.hasError());
        assertEquals(25.0, heizung.getZielTemp());
    }
}
