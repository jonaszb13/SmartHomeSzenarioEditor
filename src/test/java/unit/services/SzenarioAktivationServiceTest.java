package unit.services;

import data.services.datenServices.DataAccess;
import data.services.datenServices.DatabaseCreationService;
import data.services.objektServices.SzenarioAktivationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SzenarioAktivationServiceTest {
    private static DataAccess dataAccess;
    private static SzenarioAktivationService szenarioAktivationService;

    @BeforeAll
    static void setUp() throws SQLException {
        DataAccess.setTest(true);
        DatabaseCreationService.createDatabase();
        dataAccess = DataAccess.getInstance();
        szenarioAktivationService = SzenarioAktivationService.getInstance();
    }

    @Test
    void testAktiviereSzenario() {
        //TODO
    }

    @Test
    void testDeaktiviereSzenario() {
        //TODO
    }


    @AfterEach
    void cleanUp() throws SQLException {
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM SZENARIEN_INHALT");
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM SZENARIEN");
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM GERAETE_WERTE");
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM GERAETE");
        //language=SQL
        dataAccess.executeTestUpdate("DELETE FROM RAEUME");
    }
}
