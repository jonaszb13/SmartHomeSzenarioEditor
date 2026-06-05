package unit.models.fachobjekte.geraetearten;

import data.models.fachobjekte.Merkmalbezeichnung;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.geraetearten.Luefter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.statusmeldungen.StatusLog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LuefterTest {

    private Luefter luefter;

    @BeforeEach
    public void createObject() {
        luefter = new Luefter(UUID.randomUUID(), "Luefter", new Raum(UUID.randomUUID(), "Raum"), 27.0, true);
        StatusLog.clear();
    }

    @Test
    void updateValueGueltigerWert() {
        final String key = Merkmalbezeichnung.STAERKE.getBezeichnung();
        final String value = "30.3";
        luefter.updateValue(key, value);
        assertEquals(30.3, luefter.getStaerke());
        assertEquals("30,3", luefter.getValues().get(Merkmalbezeichnung.STAERKE.getBezeichnung()));
    }

    @Test
    void updateValueUngueltigerWert() {
        final String key = Merkmalbezeichnung.STAERKE.getBezeichnung();
        final String value = "Test";
        assertThrows(NumberFormatException.class, () -> {
            luefter.updateValue(key, value);
        });
        assertFalse(StatusLog.hasError());
        assertEquals(27.0, luefter.getStaerke());
        assertEquals("27,0", luefter.getValues().get(Merkmalbezeichnung.STAERKE.getBezeichnung()));
    }

    @Test
    void updateValueSchluesselNichtVorhanden() {
        final String key = "falsch";
        final String value = "30.3";
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            luefter.updateValue(key, value);
        });
        assertEquals("Ungültiger Schlüssel in der Datenbank", exception.getMessage());
        assertTrue(StatusLog.hasError());
        assertEquals(27.0, luefter.getStaerke());
        assertEquals("27,0", luefter.getValues().get(Merkmalbezeichnung.STAERKE.getBezeichnung()));
    }

    @Test
    void isGueltigeAttributeKorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.STAERKE.getBezeichnung(), "28,0");
        attributeMap.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        assertTrue(luefter.isGueltigeAttribute(attributeMap));
        assertFalse(StatusLog.hasError());
        assertEquals(27.0, luefter.getStaerke());
    }

    @Test
    void isGueltigeAttributeTechnischInkorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.STAERKE.getBezeichnung(), "zweiunddreißig");
        attributeMap.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        assertFalse(luefter.isGueltigeAttribute(attributeMap));
        assertTrue(StatusLog.hasError());
        assertEquals(27.0, luefter.getStaerke());
    }

    @Test
    void isGueltigeAttributeFachlichInkorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.STAERKE.getBezeichnung(), "112,0");
        attributeMap.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        assertFalse(luefter.isGueltigeAttribute(attributeMap));
        assertTrue(StatusLog.hasError());
        assertEquals(27.0, luefter.getStaerke());
    }
}
