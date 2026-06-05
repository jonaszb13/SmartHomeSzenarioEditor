package unit.models.fachobjekte.geraetearten;

import data.models.fachobjekte.Merkmalbezeichnung;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.geraetearten.Sensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.statusmeldungen.StatusLog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SensorTest {

    private Sensor luefter;

    @BeforeEach
    public void createObject() {
        luefter = new Sensor(UUID.randomUUID(), "Luefter", new Raum(UUID.randomUUID(), "Raum"), false, true);
        StatusLog.clear();
    }

    @Test
    void updateValueGueltigerWert() {
        final String key = Merkmalbezeichnung.EINGESCHALTET.getBezeichnung();
        luefter.updateValue(key, "false");
        assertFalse(luefter.isAusschlag());
        assertFalse(luefter.isEingeschaltet());
    }

    @Test
    void updateValueSchluesselNichtVorhanden() {
        final String key = "falsch";
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            luefter.updateValue(key, "false");
        });
        assertEquals("Ungültiger Schlüssel in der Datenbank", exception.getMessage());
        assertTrue(StatusLog.hasError());
        assertFalse(luefter.isAusschlag());
        assertTrue(luefter.isEingeschaltet());
    }

    @Test
    void isGueltigeAttributeKorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung(), "false");
        attributeMap.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        assertTrue(luefter.isGueltigeAttribute(attributeMap));
        assertFalse(StatusLog.hasError());
        assertTrue(luefter.isEingeschaltet());
        assertFalse(luefter.isAusschlag());
        assertFalse(Boolean.parseBoolean(luefter.getValues().get(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung())));
    }

    @Test
    void isGueltigeAttributeTechnischInkorrekt() {
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(Merkmalbezeichnung.AUSSCHLAG.getBezeichnung(), "falsch");
        attributeMap.put(Merkmalbezeichnung.EINGESCHALTET.getBezeichnung(), "false");
        assertTrue(luefter.isGueltigeAttribute(attributeMap));
        assertFalse(StatusLog.hasError());
        assertTrue(luefter.isEingeschaltet());
        assertFalse(luefter.isAusschlag());
    }
}

