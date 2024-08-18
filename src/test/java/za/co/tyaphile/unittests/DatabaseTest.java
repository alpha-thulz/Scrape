package za.co.tyaphile.unittests;

import org.junit.jupiter.api.*;
import za.co.tyaphile.connect.Connect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseTest {
    private static Connection conn;
    private static String dbName = "test_db", dbTable = "test_table", username = "root", password = "P@ssi0nat3ly";

    @BeforeAll
    static void setUp() throws Exception {
        Connect.createDatabase(dbName, username, password);
    }

    @AfterAll
    static void tearDown() throws Exception {
        String sql = "DROP SCHEMA " + dbName;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        conn.close();
    }

    @Order(1)
    @Test
    public void testCreatable() {
        try {
            conn = Connect.getConnection(dbName, username, password);
            String sql = "CREATE TABLE " + dbTable + " ( `id` int(11) NOT NULL AUTO_INCREMENT, `data` varchar(255) DEFAULT NULL, PRIMARY KEY (`id`) ) ENGINE=MEMORY;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Order(2)
    @Test
    public void testConnection() throws SQLException {
        assertFalse(conn.isClosed());
        assertTrue(conn.getAutoCommit());
    }

    @Order(3)
    @Test
    public void testInsert() throws SQLException {
        String sql = "INSERT INTO " + dbTable + " (data) VALUES (?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 1; i <= 100; i++) {
            ps.setString(1, "test_" + i);
            assertEquals(1, ps.executeUpdate());
        }
    }

    @Order(4)
    @Test
    public void testCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + dbTable;
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        assertTrue(rs.next());
        assertEquals(100, rs.getInt(1));
    }

    @Order(5)
    @Test
    void testSelect() throws SQLException {
        String sql = "SELECT * FROM " + dbTable;
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        int count = 1;
        while (rs.next()) {
            assertEquals(count++, rs.getInt(1));
        }
    }

    @Test
    @Order(6)
    void testSelectByPrimaryKey() throws SQLException {
        String sql = "SELECT * FROM " + dbTable + " WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, 10);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            assertEquals(10, rs.getInt(1));
            assertEquals("test_10", rs.getString(2));
        }
    }
}
