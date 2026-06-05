package unit.models.fachobjekte.geraetearten;

import data.models.fachobjekte.Merkmalbezeichnung;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.geraetearten.Rollladen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.statusmeldungen.StatusLog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RollladenTest {

    private Rollladen rollladen;

    @BeforeEach
    public void createObject() {
        rollladen = new Rollladen(UUID.randomUUID(), "Rollladen", new Raum(UUID.randomUUID(), "Raum"), 27.0, 28.0);
        StatusLog.clear();
    }

    @Test
    void updateValueGueltigerWert() {
        final String key = Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung();
        final String value = "30.6";
        rollladen.updateValue(key, value);
        final String key2 = Merkmalbezeichnung.NEIGUNG.getBezeichnung();
        final String value2 = "30.3";
        rollladen.updateValue(key2, value2);
        assertEquals(30.6, rollladen.getSchliessstatus());
        assertEquals("30,6", rollladen.getValues().get(Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung()));
        assertEquals(30.3, rollladen.getNeigung());
        assertEquals("30,3", rollladen.getValues().get(Merkmalbezeichnung.NEIGUNG.getBezeichnung()));
    }

    @Test
    void updateValueUngueltigerWert() {
        final String key = Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung();
        final String value = "Test";
        assertThrows(NumberFormatException.class, () -> {
            rollladen.updateValue(key, value);
        });
        assertFalse(StatusLog.hasError());
        assertEquals(27.0, rollladen.getSchliessstatus());
        assertEquals("27,0", rollladen.getValues().get(Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung()));
    }

    @Test
    void updateValueSchluesselNichtVorhanden() {
        final String key = "falsch";
        final String value = "30.3";
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            rollladen.updateValue(key, value);
        });
        assertEquals("Ungültiger Schlüssel in der Datenbank", exception.getMessage());
        assertTrue(StatusLog.hasError());
        assertEquals(27.0, rollladen.getSchliessstatus());
        assertEquals("27,0", rollladen.getValues().get(Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung()));
    }

    @Test
    void isGueltigeAttributeKorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung(), "28,0");
        attributeMap.put(Merkmalbezeichnung.NEIGUNG.getBezeichnung(), "40");
        assertTrue(rollladen.isGueltigeAttribute(attributeMap));
        assertFalse(StatusLog.hasError());
        assertEquals(27.0, rollladen.getSchliessstatus());
    }

    @Test
    void isGueltigeAttributeTechnischInkorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung(), "zweiunddreißig");
        attributeMap.put(Merkmalbezeichnung.NEIGUNG.getBezeichnung(), "40");
        assertFalse(rollladen.isGueltigeAttribute(attributeMap));
        assertTrue(StatusLog.hasError());
        assertEquals(27.0, rollladen.getSchliessstatus());
    }

    @Test
    void isGueltigeAttributeFachlichInkorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.SCHLIESSSTATUS.getBezeichnung(), "112,0");
        attributeMap.put(Merkmalbezeichnung.NEIGUNG.getBezeichnung(), "90");
        assertFalse(rollladen.isGueltigeAttribute(attributeMap));
        assertTrue(StatusLog.hasError());
        assertEquals(27.0, rollladen.getSchliessstatus());
    }
}
