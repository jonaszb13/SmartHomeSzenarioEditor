package unit.services;

import data.services.datenServices.DataAccess;
import data.services.datenServices.DatabaseCreationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseCreationServiceTest {

    @BeforeAll
    static void setUp() {
        DataAccess.setTest(true);
    }

    @Test
    void testCreateDatabase() throws SQLException {
        DatabaseCreationService.createDatabase();
        int anzahlTabellen = DataAccess.getInstance().getTestValue("""
                SELECT COUNT(*)
                FROM INFORMATION_SCHEMA.TABLES
                WHERE TABLE_SCHEMA = 'PUBLIC'
                """);
        assertEquals(5, anzahlTabellen);
    }

}
