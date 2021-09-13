package app.sql;

import ogame.utils.log.AppLog;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connector {
    private static final String URL = "jdbc:mysql://localhost:3306/ogamebot";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    /** PL
     * Nawiązuje połączenie z bazą danych.
     * EN
     * Gets connection with database.
     * @return Return false if connecting fail.
     */
    public static Connection getConnection(){
        AppLog.print(Connector.class.getName(),0,"Start connecting to database.");
        try{
            String driver = "com.mysql.cj.jdbc.Driver";
            Class.forName(driver);

            Connection conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            AppLog.print(Connector.class.getName(),0,"Connected to database success.");
            return conn;
        }
        catch(Exception e){e.printStackTrace();}
        AppLog.print(Connector.class.getName(),1,"Connected to database fails.");
        return null;
    }
}
