package unit.models;

import data.models.Model;
import data.services.datenservices.DataAccess;
import data.services.datenservices.DatabaseCreationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.statusmeldungen.StatusLog;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    private static Model model;

    @BeforeAll
    static void setUp() throws SQLException {
        DataAccess.setTest(true);
        DatabaseCreationService.createDatabase();
        StatusLog.clear();
        model = Model.getInstance();
    }

    @Test
    void getInstance_gibtNichtNullInstanzZurueck() {
        assertNotNull(model);
    }

    @Test
    void getInstance_gibtImmerGleicheInstanzZurueck() {
        assertSame(model, Model.getInstance());
    }

    @Test
    void konstruktor_statusbereichIstStatusLogInstanz() {
        assertSame(StatusLog.getInstance(), model.getStatusbereich());
    }

    @Test
    void konstruktor_raumMapIstInitialisiert() {
        assertNotNull(model.getRaumMap());
    }

    @Test
    void konstruktor_geraeteMapIstInitialisiert() {
        assertNotNull(model.getGeraete());
    }

    @Test
    void konstruktor_szenarioMapIstInitialisiert() {
        assertNotNull(model.getSzenarioMap());
    }

    @Test
    void konstruktor_keinFehlerBeimLaden() {
        assertFalse(StatusLog.hasError());
    }
}
