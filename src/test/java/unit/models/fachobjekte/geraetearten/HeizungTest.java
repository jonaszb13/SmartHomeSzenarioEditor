package unit.models.fachobjekte.geraetearten;

import data.models.fachobjekte.Merkmalbezeichnung;
import data.models.fachobjekte.Raum;
import data.models.fachobjekte.geraetearten.Heizung;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HeizungTest {
    Heizung heizungKorrekt = new Heizung(UUID.randomUUID(), "Heizung Korrekt", new Raum(UUID.randomUUID(), "Raum"), 25.0);

    @Test
    void updateValueGueltigerWert() {
        String key = Merkmalbezeichnung.ZIELTEMP.getBezeichnung();
        String value = "30.3";
        heizungKorrekt.updateValue(key, value);
        assertEquals(30.3, heizungKorrekt.getZielTemp());
        assertEquals("30,3", heizungKorrekt.getValues().get(Merkmalbezeichnung.ZIELTEMP.getBezeichnung()));
    }

    @Test
    void updateValueUngueltigerWert() {
        String key = Merkmalbezeichnung.ZIELTEMP.getBezeichnung();
        String value = "Test";
        assertThrows(NumberFormatException.class, () -> {
            heizungKorrekt.updateValue(key, value);
        });
        assertEquals(25.0, heizungKorrekt.getZielTemp());
        assertEquals("25,0", heizungKorrekt.getValues().get(Merkmalbezeichnung.ZIELTEMP.getBezeichnung()));
    }

    @Test
    void updateValueSchluesselNichtVorhanden() {
        String key = "falsch";
        String value = "30.3";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            heizungKorrekt.updateValue(key, value);
        });
        assertEquals("Ungültiger Schlüssel in der Datenbank", exception.getMessage());
        assertEquals(25.0, heizungKorrekt.getZielTemp());
        assertEquals("25,0", heizungKorrekt.getValues().get(Merkmalbezeichnung.ZIELTEMP.getBezeichnung()));
    }
}
