package unit.services;

import data.services.datenServices.DataAccess;
import data.services.objektServices.GeraetObjektService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class GeraetObjektServiceTest {
    static GeraetObjektService geraetObjektService;

    @BeforeAll
    public static void setUp() {
        DataAccess.setTest(true);
        try {
            geraetObjektService = GeraetObjektService.getInstance();
        } catch (SQLException e) {
            assert false;
        }
    }

    @Test
    void testAddGeraet() {
        //TODO
    }

    @Test
    void testDeleteGeraet() {
        //TODO
    }

    @Test
    void testUpdateGeraet() {
        //TODO Raum
        //TODO Name
    }

    @Test
    void testUpdateGeraetWerte() {
        //TODO
    }
}
