module com.example.internetcommerce {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.postgresql.jdbc;
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.logging.log4j;
    requires AnimateFX;
    requires junit;


    opens com.example.internetcommerce to javafx.fxml;
    exports com.example.internetcommerce.server;
    exports com.example.internetcommerce.database;
    exports com.example.internetcommerce.client;
    opens com.example.internetcommerce.client to javafx.fxml;
    exports com.example.internetcommerce.password;
    exports com.example.internetcommerce.client.controller.common;
    opens com.example.internetcommerce.client.controller.common to javafx.fxml;
    exports com.example.internetcommerce.client.controller.admin;
    opens com.example.internetcommerce.client.controller.admin to javafx.fxml;
    exports com.example.internetcommerce.client.controller.user;
    opens com.example.internetcommerce.client.controller.user to javafx.fxml;
    exports com.example.internetcommerce.client.controller.manager;
    opens com.example.internetcommerce.client.controller.manager to javafx.fxml;
    exports com.example.internetcommerce.models;
    opens com.example.internetcommerce.models to javafx.fxml;
}