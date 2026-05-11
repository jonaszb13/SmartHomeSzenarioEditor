package unit.services;

import data.services.datenServices.DataAccess;
import data.services.datenServices.DatabaseCreationService;
import data.services.datenServices.SzenarioDataService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SzenarioDataServiceTest {
    static DataAccess dataAccess;
    static SzenarioDataService szenarioDataService;

    @BeforeAll
    static void setup() {
        DataAccess.setTest(true);
        try {
            DatabaseCreationService.createDatabase();
            dataAccess = DataAccess.getInstance();
            szenarioDataService = SzenarioDataService.getInstance();
        } catch (SQLException e) {
            assert false;
        }
    }

    @BeforeEach
    void setUp() {
        try {
            //language=SQL
            dataAccess.executeTestUpdate("DELETE FROM SZENARIEN");
            //language=SQL
            dataAccess.executeTestUpdate("DELETE FROM GERAETE_WERTE");
            //language=SQL
            dataAccess.executeTestUpdate("DELETE FROM GERAETE");
        } catch (SQLException e) {
            assert false;
        }
    }

    @Test
    void testAddSzenario() {
        //TODO
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
