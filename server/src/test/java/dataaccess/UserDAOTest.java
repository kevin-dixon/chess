package dataaccess;

import dataaccess.sqldatabase.UserSqlDAO;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTest {

    private UserSqlDAO userDao;
    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException, DataAccessException {
        userDao = new UserSqlDAO();
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
            stmt.executeUpdate("DELETE FROM users");
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "username VARCHAR(255) PRIMARY KEY, " +
                            "password VARCHAR(255) NOT NULL, " +
                            "email VARCHAR(255) NOT NULL);");
        }
    }

    @Test
    @Order(1)
    public void testAddUserPositive() throws SQLException, DataAccessException {
        UserData userData = new UserData("testUser", "password", "test@mail.com");
        UserData result = userDao.addUser(userData);

        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertEquals("test@mail.com", result.email());
    }

    @Test
    @Order(2)
    public void testAddUserNegative() {
        try {
            UserData userData = new UserData("testUser", null, "test@mail.com");
            userDao.addUser(userData);
            fail("Exception should have been thrown");
        } catch (SQLException | DataAccessException e) {
            assertTrue(e instanceof SQLException || e instanceof DataAccessException);
        }
    }

    @Test
    @Order(3)
    public void testGetUserPositive() throws SQLException, DataAccessException {
        UserData userData = new UserData("testUser", "password", "test@mail.com");
        userDao.addUser(userData);

        UserData result = userDao.getUser("testUser");
        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertEquals("test@mail.com", result.email());
    }

    @Test
    @Order(4)
    public void testGetUserNegative() throws SQLException, DataAccessException {
        UserData result = userDao.getUser("nonexistentUser");
        assertNull(result);
    }

    @Test
    @Order(5)
    public void testDeleteAllUsersPositive() throws SQLException, DataAccessException {
        UserData userData1 = new UserData("testUser1", "password1", "test1@mail.com");
        UserData userData2 = new UserData("testUser2", "password2", "test2@mail.com");
        userDao.addUser(userData1);
        userDao.addUser(userData2);

        userDao.deleteAllUsers();

        assertNull(userDao.getUser("testUser1"));
        assertNull(userDao.getUser("testUser2"));
    }

    @Test
    @Order(6)
    public void testDeleteAllUsersNegative() {
        try {
            userDao.deleteAllUsers();
            // No exception is expected, as deleting all users should be a no-op if the table is already empty
        } catch (SQLException | DataAccessException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    @Order(7)
    public void testVerifyUserPositive() throws SQLException, DataAccessException {
        UserData userData = new UserData("testUser", "password", "test@mail.com");
        userDao.addUser(userData);

        boolean result = userDao.verifyUser("testUser", "password");
        assertTrue(result);
    }

    @Test
    @Order(8)
    public void testVerifyUserNegative() throws SQLException, DataAccessException {
        UserData userData = new UserData("testUser", "password", "test@mail.com");
        userDao.addUser(userData);

        boolean result = userDao.verifyUser("testUser", "wrongPassword");
        assertFalse(result);
    }
}