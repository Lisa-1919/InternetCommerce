package com.example.internetcommerce.database;

import java.util.Properties;
import java.sql.*;

public class StoreDataBase implements DBInterface{

    private static Connection connection;
    private static Statement statement;

    static {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/OnlineStore";
            Properties properties = new Properties();
            properties.put("user", "postgres");
            properties.put("password", "WC4ty37xd3");
            connection = DriverManager.getConnection(url, properties);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static Connection getInstance() {
        return connection;
    }

    @Override
    public void insert(String sqlString){
        try {
            statement.executeUpdate(sqlString);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void delete(String sqlString) {
        try{
            statement.executeUpdate(sqlString);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(String sqlString) {
        try {
            statement.executeUpdate(sqlString);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
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

    @Override
    public void close() {
        try{
            connection.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
