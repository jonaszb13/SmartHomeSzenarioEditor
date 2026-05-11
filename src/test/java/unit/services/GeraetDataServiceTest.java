package unit.services;

import data.services.datenServices.DataAccess;
import data.services.datenServices.DatabaseCreationService;
import data.services.datenServices.GeraetDataService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class GeraetDataServiceTest {
    static DataAccess dataAccess;
    static GeraetDataService geraetDataService;

    @BeforeAll
    static void setup() {
        DataAccess.setTest(true);
        try {
            DatabaseCreationService.createDatabase();
            dataAccess = DataAccess.getInstance();
            geraetDataService = GeraetDataService.getInstance();
        } catch (SQLException e) {
            assert false;
        }
    }

    @BeforeEach
    void setUp() {
        try {
            //language=SQL
            DataAccess.getInstance().executeTestUpdate("DELETE FROM GERAETE");
            DatabaseCreationService.createDatabase();
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
        //TODO
    }
}
