package Resources;

import java.sql.*;

public class DB {
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost/mmorpgDB", "root", "password");
    }
}
