package com.example.internetcommerce.client.controller.common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CustomProductController extends AnchorPane {

    ProductFormController productFormController;

    public CustomProductController() {
        super();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/internetcommerce/productForm.fxml"));
            productFormController = new ProductFormController();
            loader.setController(productFormController);
            Node node = loader.load();

            this.getChildren().add(node);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
