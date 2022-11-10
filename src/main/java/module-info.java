module com.example.internetcommerce {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.postgresql.jdbc;
    requires java.sql;


    opens com.example.internetcommerce to javafx.fxml;
    exports com.example.internetcommerce.server;
    exports com.example.internetcommerce.database;
    exports com.example.internetcommerce.client;
    opens com.example.internetcommerce.client to javafx.fxml;
    exports com.example.internetcommerce.password;
}