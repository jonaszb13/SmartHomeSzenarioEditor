package unit.services;

import data.services.datenServices.DataAccess;
import data.services.objektServices.SzenarioObjektService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SzenarioObjektServiceTest {
    static SzenarioObjektService szenarioObjektService;

    @BeforeAll
    static void setUp() {
        DataAccess.setTest(true);
        try {
            szenarioObjektService = SzenarioObjektService.getInstance();
        } catch (SQLException e) {
            assert false;
        }
    }

    @Test
    void testAddSzenario() {
        //TODO
    }

    @Test
    void testUpdateSzenario() {
        //TODO 3 methoden
    }

    @Test
    void testDeleteSzenario() {
        //TODO
    }

    @Test
    void testAddSzenarioInhalt() {
        //TODO
    }

    @Test
    void testUpdateSzenarioInhalt() {
        //TODO
    }

    @Test
    void testDeleteSzenarioInhalt() {
        //TODO
    }
}
