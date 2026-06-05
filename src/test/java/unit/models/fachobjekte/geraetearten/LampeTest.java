package unit.models.fachobjekte.geraetearten;

import data.models.fachobjekte.Merkmalbezeichnung;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.geraetearten.Lampe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.statusmeldungen.StatusLog;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LampeTest {

    private Lampe lampe;

    @BeforeEach
    public void createObject() {
        lampe = new Lampe(UUID.randomUUID(), "Lampe", new Raum(UUID.randomUUID(), "Raum"), 25.0, Color.WHITE, true);
        StatusLog.clear();
    }

    @Test
    void updateValueGueltigerWert() {
        final String key = Merkmalbezeichnung.HELLIGKEIT.getBezeichnung();
        final String value = "30.3";
        lampe.updateValue(key, value);
        final String key2 = Merkmalbezeichnung.FARBE.getBezeichnung();
        lampe.updateValue(key2, "#FF69B4");
        final String key3 = Merkmalbezeichnung.EINGESCHALTET.getBezeichnung();
        lampe.updateValue(key3, "false");
        assertEquals(30.3, lampe.getHelligkeit());
        assertEquals("30,3", lampe.getValues().get(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung()));
        assertEquals(Color.decode("#FF69B4"), lampe.getFarbe());
        assertFalse(lampe.isEingeschaltet());
    }

    @Test
    void updateValueUngueltigerWert() {
        final String key = Merkmalbezeichnung.HELLIGKEIT.getBezeichnung();
        final String value = "Test";
        assertThrows(NumberFormatException.class, () -> {
            lampe.updateValue(key, value);
        });
        assertFalse(StatusLog.hasError());
        assertEquals(25.0, lampe.getHelligkeit());
        assertEquals("25,0", lampe.getValues().get(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung()));
    }

    @Test
    void updateValueSchluesselNichtVorhanden() {
        final String key = "falsch";
        final String value = "30.3";
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            lampe.updateValue(key, value);
        });
        assertEquals("Ungültiger Schlüssel in der Datenbank", exception.getMessage());
        assertTrue(StatusLog.hasError());
        assertEquals(25.0, lampe.getHelligkeit());
        assertEquals("25,0", lampe.getValues().get(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung()));
    }

    @Test
    void isGueltigeAttributeKorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung(), "88,0");
        attributeMap.put(Merkmalbezeichnung.FARBE.getBezeichnung(), "#FF69B4");
        attributeMap.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        assertTrue(lampe.isGueltigeAttribute(attributeMap));
        assertFalse(StatusLog.hasError());
        assertEquals(25.0, lampe.getHelligkeit());
    }

    @Test
    void isGueltigeAttributeTechnischInkorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung(), "zweiunddreißig");
        attributeMap.put(Merkmalbezeichnung.FARBE.getBezeichnung(), "#FF69B4");
        attributeMap.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        assertFalse(lampe.isGueltigeAttribute(attributeMap));
        assertTrue(StatusLog.hasError());
        assertEquals(25.0, lampe.getHelligkeit());
    }

    @Test
    void isGueltigeAttributeFachlichInkorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.HELLIGKEIT.getBezeichnung(), "112,0");
        attributeMap.put(Merkmalbezeichnung.FARBE.getBezeichnung(), "#FF69B4");
        attributeMap.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        assertFalse(lampe.isGueltigeAttribute(attributeMap));
        assertTrue(StatusLog.hasError());
        assertEquals(25.0, lampe.getHelligkeit());
    }
}
