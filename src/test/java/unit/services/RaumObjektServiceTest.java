package unit.services;

import data.services.datenServices.DataAccess;
import data.services.objektServices.RaumObjektService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class RaumObjektServiceTest {
    static RaumObjektService raumObjektService;

    @BeforeAll
    static void setUp() {
        DataAccess.setTest(true);
        try {
            raumObjektService = RaumObjektService.getInstance();
        } catch (SQLException e) {
            assert false;
        }
    }

    @Test
    void testAddRaum() {
        //TODO
    }

    @Test
    void testUpdateRaum() {
        //TODO
    }

    @Test
    void testDeleteRaum() {
        //TODO
    }
}
