package com.example.internetcommerce.database;

import javax.xml.transform.Result;
import java.sql.ResultSet;

public interface DBInterface {

    public void insert(String sqlString);

    public void delete(String sqlString);

    public void update(String sqlString);

    public ResultSet select(String sqlString);

    public void close();
}
