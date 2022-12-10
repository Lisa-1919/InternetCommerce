package com.example.internetcommerce.database;

import java.util.Properties;
import java.sql.*;

public class StoreDataBase{

    private static StoreDataBase instance;
    private static Connection connection;
    private static Statement statement;
    public static synchronized StoreDataBase getInstance() {
        if (instance == null) {
            instance = new StoreDataBase();
        }
        return instance;
    }

    public StoreDataBase() {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/OnlineStore";
            Properties properties = new Properties();
            properties.put("user", "postgres");
            properties.put("password", "WC4ty37xd3");
            this.connection = DriverManager.getConnection(url, properties);
            this.statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void insert(String sqlString){
        try {
            statement.executeUpdate(sqlString);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void delete(String sqlString) {
        try{
            statement.executeUpdate(sqlString);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(String sqlString) {
        try {
            statement.executeUpdate(sqlString);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public ResultSet select(String sqlString) {
        ResultSet resultSet = null;
        try{
            resultSet = statement.executeQuery(sqlString);
            resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultSet;
    }

    public void close() {
        try{
            connection.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
