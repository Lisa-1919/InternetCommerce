package com.example.internetcommerce.client.controller.common;

import com.example.internetcommerce.models.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductFormController implements Initializable {
    @FXML
    private Button btAddToBasket;

    @FXML
    private ImageView imgField;

    @FXML
    private Label nameLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private AnchorPane product;

    @FXML
    void addToBasket(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public Button getBtAddToBasket() {
        return btAddToBasket;
    }

    public void setBtAddToBasket(Button btAddToBasket) {
        this.btAddToBasket = btAddToBasket;
    }

    public ImageView getImgField() {
        return imgField;
    }

    public void setImgField(ImageView imgField) {
        this.imgField = imgField;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(Label nameLabel) {
        this.nameLabel = nameLabel;
    }

    public Label getPriceLabel() {
        return priceLabel;
    }

    public void setPriceLabel(Label priceLabel) {
        this.priceLabel = priceLabel;
    }

}
