package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // local dev DB – change URL/user/pass to your setup
    private static final String URL =
            "jdbc:mysql://localhost:3306/mazdadb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";          // your MySQL user
    private static final String PASS = "P1CK@X33"; // your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}