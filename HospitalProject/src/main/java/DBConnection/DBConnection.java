/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author 3DO TECH
 */
public class DBConnection {
    private static final String urlConnect = "jdbc:sqlserver://HIP\\HIEU:1433;databasename=HospitalDB;user=sa;password=123;characterEncoding=UTF-8;encrypt=true;trustServerCertificate=true;";
    public static Connection connect() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(urlConnect);
            System.out.println("Connect ok");
            return conn;
        } catch(ClassNotFoundException | SQLException ex) {
            System.out.println(ex);
        } 
        return null;
    }
    
    public static void main(String args[]) {
        System.out.println(connect());
    }
}
