package dataaccess;

import dataaccess.sqldatabase.AuthSqlDAO;
import model.AuthData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthDAOTest {

    private AuthSqlDAO authDao;
    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException, DataAccessException {
        authDao = new AuthSqlDAO();
        connection = DatabaseManager.getConnection();

        // Ensure the database is clean and tables are created before each test
        createTables();
        clearDatabase();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Clear the database after each test
        clearDatabase();
        connection.close();
    }

    private void clearDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM auths");
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS auths (" +
                            "authToken VARCHAR(255) PRIMARY KEY, " +
                            "username VARCHAR(255) NOT NULL);");
        }
    }

    @Test
    @Order(1)
    public void testAddAuthPositive() throws SQLException, DataAccessException {
        AuthData authData = new AuthData("testToken", "testUser");
        AuthData result = authDao.addAuth(authData);

        assertNotNull(result);
        assertEquals("testToken", result.authToken());
        assertEquals("testUser", result.username());
    }

    @Test
    @Order(2)
    public void testAddAuthNegative() {
        try {
            AuthData authData = new AuthData(null, "testUser");
            authDao.addAuth(authData);
            fail("Exception should have been thrown");
        } catch (SQLException | DataAccessException e) {
            assertTrue(e instanceof SQLException || e instanceof DataAccessException);
        }
    }

    @Test
    @Order(3)
    public void testGetAuthPositive() throws SQLException, DataAccessException {
        AuthData authData = new AuthData("testToken", "testUser");
        authDao.addAuth(authData);

        AuthData result = authDao.getAuth("testToken");
        assertNotNull(result);
        assertEquals("testToken", result.authToken());
        assertEquals("testUser", result.username());
    }

    @Test
    @Order(4)
    public void testGetAuthNegative() throws SQLException, DataAccessException {
        AuthData result = authDao.getAuth("nonexistentToken");
        assertNull(result);
    }

    @Test
    @Order(5)
    public void testDeleteAuthPositive() throws SQLException, DataAccessException {
        AuthData authData = new AuthData("testToken", "testUser");
        authDao.addAuth(authData);

        authDao.deleteAuth("testToken");

        AuthData result = authDao.getAuth("testToken");
        assertNull(result);
    }

    @Test
    @Order(6)
    public void testDeleteAuthNegative() {
        try {
            authDao.deleteAuth("nonexistentToken");
            // No exception is expected, as deleting a non-existent token should be a no-op
        } catch (SQLException | DataAccessException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    @Order(7)
    public void testDeleteAllAuthsPositive() throws SQLException, DataAccessException {
        AuthData authData1 = new AuthData("testToken1", "testUser1");
        AuthData authData2 = new AuthData("testToken2", "testUser2");
        authDao.addAuth(authData1);
        authDao.addAuth(authData2);

        authDao.deleteAllAuths();

        assertNull(authDao.getAuth("testToken1"));
        assertNull(authDao.getAuth("testToken2"));
    }

    @Test
    @Order(8)
    public void testDeleteAllAuthsNegative() {
        try {
            authDao.deleteAllAuths();
            // No exception is expected, as deleting all auths should be a no-op if the table is already empty
        } catch (SQLException | DataAccessException e) {
            fail("Exception should not have been thrown");
        }
    }
}