package unit.services;

import data.services.datenServices.DataAccess;
import data.services.datenServices.DatabaseCreationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SzenarioDataServiceTest {
    static DataAccess dataAccess;

    @BeforeAll
    static void setup() {
        DataAccess.setTest(true);
        try {
            DatabaseCreationService.createDatabase();
            dataAccess = DataAccess.getInstance();
        } catch (SQLException e) {
            assert false;
        }
    }

    @BeforeEach
    void setUp() {
        try {
            //language=SQL
            DataAccess.getInstance().executeTestUpdate("DELETE FROM SZENARIEN");
            DatabaseCreationService.createDatabase();
        } catch (SQLException e) {
            assert false;
        }
    }

    @Test
    void testGetSzenario() {

    }
}
