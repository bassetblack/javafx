package com.example.demo3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddProductDialogController {

    @FXML private Button barcodeButton;
    @FXML private TextField nameField;
    @FXML private TextField quantityField;
    @FXML private TextField buyingPriceField;
    @FXML private DatePicker expiryDatePicker;
    @FXML private TextField unitPriceField;
    @FXML private TextField price2Field;
    @FXML private TextField price3Field;

    private List<String> newBarcodes = new ArrayList<>();
    private Product newProduct = null;


    @FXML
    private void handleShowBarcodesDialog() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("barcodes-dialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Manage Barcodes");

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(barcodeButton.getScene().getWindow());

            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.setScene(new Scene(page));

            BarcodesDialogController controller = loader.getController();

            controller.initializeWithBarcodes(new ArrayList<>(newBarcodes));

            dialogStage.showAndWait();

            this.newBarcodes = controller.getBarcodes();

            if (!newBarcodes.isEmpty()) {
                barcodeButton.setText(newBarcodes.size() + " barcode(s) added");
            } else {
                barcodeButton.setText("Add Barcode(s)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProductData(Product product) {
           newProduct = product;


        nameField.setText(product.getName());
        quantityField.setText(String.valueOf(product.getQuantity()));
        buyingPriceField.setText(String.valueOf(product.getBuyingPrice()));
        expiryDatePicker.setValue(product.getExpiryDate());
        unitPriceField.setText(String.valueOf(product.getUnitPrice()));
        price2Field.setText(String.valueOf(product.getPrice2()));
        price3Field.setText(String.valueOf(product.getPrice3()));


        newBarcodes.addAll(product.getBarcodes());
        barcodeButton.setText(newBarcodes.size() + " barcode(s) added");
    }

    @FXML
    private void handleAddButtonAction() {
           if (newBarcodes.isEmpty() || nameField.getText().isEmpty() || quantityField.getText().isEmpty()) {
            System.err.println("Validation Failed: Barcode, Name, and Quantity are required.");
            return;
        }

        try {

            String name = nameField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double buyingPrice = Double.parseDouble(buyingPriceField.getText());
            LocalDate expiryDate = expiryDatePicker.getValue();
            double unitPrice = Double.parseDouble(unitPriceField.getText());
            double price2 = Double.parseDouble(price2Field.getText());
            double price3 = Double.parseDouble(price3Field.getText());

            if (newProduct == null || newProduct.getId() == 0) {

                newProduct = new Product(0, newBarcodes, name, quantity, buyingPrice, expiryDate, unitPrice, price2, price3);


                int generatedId = DatabaseUtil.insertProduct(newProduct);
                if (generatedId == -1) {
                    System.err.println("Failed to insert the product into the database.");
                    return;
                }

                newProduct.setId(generatedId);
            } else {

                newProduct.setName(name);
                newProduct.setBarcodes(newBarcodes);
                newProduct.setQuantity(quantity);
                newProduct.setBuyingPrice(buyingPrice);
                newProduct.setExpiryDate(expiryDate);
                newProduct.setUnitPrice(unitPrice);
                newProduct.setPrice2(price2);
                newProduct.setPrice3(price3);

                boolean isUpdated = DatabaseUtil.updateProduct(newProduct);
                if (!isUpdated) {
                    System.err.println("Failed to update the product in the database.");
                    return;
                }
            }

            closeDialog();

        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in one of the fields: " + e.getMessage());
        }
    }

    public Product getNewProduct() {
        return newProduct;
    }


    @FXML
    private void handleCancelButtonAction() {
        closeDialog();
    }


    private void closeDialog() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
