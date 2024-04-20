package com.shehan.edumanage.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class  DbConnection{
    // singleton => Creational design pattern => ****
    // rule 1
    private static DbConnection dbConnection = null;

    private Connection connection;

    //rule 2
    private DbConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lms", "root", "As@2230342#");
    }

    // rule 3
    public static DbConnection getInstance() throws SQLException, ClassNotFoundException {
        if (dbConnection == null) {
            dbConnection = new DbConnection();
        }
        return dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }
}
