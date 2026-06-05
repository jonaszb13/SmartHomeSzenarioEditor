package unit.services;

import data.services.daten_services.DataAccess;
import data.services.daten_services.DatabaseCreationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseCreationServiceTest {

    private static final int EXPECTED_TABLE_COUNT = 5;

    @BeforeAll
    static void setUp() {
        DataAccess.setTest(true);
    }

    @Test
    void testCreateDatabase() throws SQLException {
        DatabaseCreationService.createDatabase();
        final CachedRowSet crs = DataAccess.getInstance().getData("""
                SELECT COUNT(*)
                FROM INFORMATION_SCHEMA.TABLES
                WHERE TABLE_SCHEMA = 'PUBLIC'
                """);
        crs.next();
        assertEquals(EXPECTED_TABLE_COUNT, crs.getInt(1));
    }
}
