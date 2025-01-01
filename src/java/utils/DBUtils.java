/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author vothimaihoa
 */
public class DBUtils {
    private static final String DB_NAME = "MvcDemo";
    private static final String DB_USER_NAME = "MSINGHIA";
    private static final String DB_PASSWORD = "12345"; // cac ban nen de la 12345 vi de PE de vay

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url = "jdbc:sqlserver://localhost:1433;databaseName=" + DB_NAME + ";integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
        conn = DriverManager.getConnection(url);
        return conn;
    }
}
