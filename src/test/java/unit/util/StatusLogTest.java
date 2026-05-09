package unit.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.statusmeldungen.Meldung;
import util.statusmeldungen.Meldungstyp;
import util.statusmeldungen.StatusLog;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatusLogTest {

    @BeforeEach
    void setUp() {
        StatusLog.clear();
    }

    @Test
    void addError_string_shouldAddFehlerMeldung() {
        StatusLog.addError("TestFehler1");
        List<Meldung> meldungen = StatusLog.getInstance().getStatusLogEintraege();
        assertEquals(1, meldungen.size());
        assertTrue(meldungen.getFirst().isError());
        assertEquals("TestFehler1", meldungen.getFirst().getMeldungstext());
    }

    @Test
    void addError_exception_shouldAddFehlerMeldungWithStackTrace() {
        Exception exception = new RuntimeException("TestExceptionFehler1");
        StatusLog.addError(exception);
        Meldung meldungen = StatusLog.getInstance().getStatusLogEintraege().getFirst();
        assertTrue(meldungen.isError());
        assertNotNull(meldungen.getStackTrace());
    }

    @Test
    void addHinweis_shouldAddHinweisMeldung() {
        StatusLog.addHinweis("TestHinweis1");
        Meldung meldungen = StatusLog.getInstance().getStatusLogEintraege().getFirst();
        assertFalse(meldungen.isError());
        assertEquals(Meldungstyp.HINWEIS.getBezeichnung(), meldungen.getMeldungsTyp());
    }

    @Test
    void hasError_withNoErrors_shoudlReturnFalse() {
        StatusLog.addHinweis("TestHinweis1");
        assertFalse(StatusLog.hasError());
    }

    @Test
    void hasError_withError_shouldReturnTrue() {
        StatusLog.addHinweis("TestHinweis1");
        StatusLog.addError("TestFehler1");
        assertTrue(StatusLog.hasError());
    }

    @Test
    void addMetadaten_shouldNotCountAsError() {
        StatusLog.addMetadaten("TestMetadaten1");
        assertFalse(StatusLog.hasError());
    }
}
