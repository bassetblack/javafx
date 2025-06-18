package com.example.demo3;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.SortType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ProductsViewController {

    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, Number> idColumn;
    @FXML private TableColumn<Product, String> barcodeColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Number> qtyColumn;
    @FXML private TableColumn<Product, Number> buyingPriceColumn;
    @FXML private TableColumn<Product, String> expiryDateColumn;
    @FXML private TableColumn<Product, Number> unitPriceColumn;
    @FXML private TableColumn<Product, Number> price2Column;
    @FXML private TableColumn<Product, Number> price3Column;

    @FXML private TextField nameSearchField;
    @FXML private TextField barcodeSearchField;

    private boolean isDeleteMode = false;
    @FXML private Button deleteButton;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;


    private final ObservableList<Product> productData = FXCollections.observableArrayList();


    private FilteredList<Product> filteredData;

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        qtyColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        buyingPriceColumn.setCellValueFactory(cellData -> cellData.getValue().buyingPriceProperty());
        unitPriceColumn.setCellValueFactory(cellData -> cellData.getValue().unitPriceProperty());
        price2Column.setCellValueFactory(cellData -> cellData.getValue().price2Property());
        price3Column.setCellValueFactory(cellData -> cellData.getValue().price3Property());


        barcodeColumn.setCellValueFactory(cellData -> cellData.getValue().mainBarcodeProperty());
        barcodeColumn.setCellFactory(column -> new BarcodeTableCell());


        expiryDateColumn.setCellValueFactory(cellData -> cellData.getValue().expiryDateProperty().asString());


        productData.addAll(DatabaseUtil.getAllProducts());


        filteredData = new FilteredList<>(productData, p -> true);


        SortedList<Product> sortedData = new SortedList<>(filteredData);


        sortedData.comparatorProperty().bind(productsTable.comparatorProperty());

        productsTable.setItems(sortedData);


        productsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        nameSearchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilter());
        barcodeSearchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilter());


        setupDeleteButton();


        addRowContextMenu();
    }


    private void setupDeleteButton() {
        deleteButton.setOnAction(event -> {

            deleteButton.setVisible(false);
            deleteButton.setManaged(false);
            confirmButton.setVisible(true);
            confirmButton.setManaged(true);
            cancelButton.setVisible(true);
            cancelButton.setManaged(true);


            isDeleteMode = true;

            productsTable.getSelectionModel().clearSelection();

            System.out.println("Select rows to delete...");
        });

        confirmButton.setOnAction(event -> {

            ObservableList<Product> selectedItems = productsTable.getSelectionModel().getSelectedItems();


            for (Product product : selectedItems) {
                boolean isDeleted = DatabaseUtil.deleteProduct(product.getId());
                if (!isDeleted) {
                    System.out.println("Failed to delete product with ID: " + product.getId());
                }
            }


            productData.removeAll(selectedItems);


            productsTable.getSelectionModel().clearSelection();


            isDeleteMode = false;


            resetButtons();


            highlightSelectedRows(false);
        });

        cancelButton.setOnAction(event -> {

            productsTable.getSelectionModel().clearSelection();


            isDeleteMode = false;


            resetButtons();


            highlightSelectedRows(false);
        });
    }

    private void highlightSelectedRows(boolean highlight) {

        if (!isDeleteMode) {
            return;
        }


        for (int i = 0; i < productsTable.getItems().size(); i++) {
            Product product = productsTable.getItems().get(i);
            TableRow<Product> row = (TableRow<Product>) productsTable.lookup("#row" + i);


            if (row != null && productsTable.getSelectionModel().isSelected(i)) {
                if (highlight) {
                    row.getStyleClass().add("highlighted-row");
                } else {
                    row.getStyleClass().remove("highlighted-row");
                }
            }
        }

        productsTable.refresh();
    }

    private void resetButtons() {
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);
        confirmButton.setVisible(false);
        confirmButton.setManaged(false);
        cancelButton.setVisible(false);
        cancelButton.setManaged(false);
    }

    private void addRowContextMenu() {

        ContextMenu contextMenu = new ContextMenu();

        // Add "Edit" menu item
        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(event -> {
            Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                try {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("add-product-dialog.fxml"));
                    Parent page = loader.load();


                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Edit Product");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(productsTable.getScene().getWindow());
                    dialogStage.initStyle(StageStyle.UNDECORATED);
                    dialogStage.setScene(new Scene(page));

                    AddProductDialogController controller = loader.getController();

                    controller.loadProductData(selectedProduct);

                    dialogStage.showAndWait();

                    Product updatedProduct = controller.getNewProduct();
                    if (updatedProduct != null) {

                        boolean isUpdated = DatabaseUtil.updateProduct(updatedProduct);
                        if (isUpdated) {

                            int index = productData.indexOf(selectedProduct);
                            productData.set(index, updatedProduct);
                        } else {
                            System.out.println("Failed to update product with ID: " + updatedProduct.getId());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> {
            Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                boolean isDeleted = DatabaseUtil.deleteProduct(selectedProduct.getId());
                if (isDeleted) {
                    productData.remove(selectedProduct);
                } else {
                    System.out.println("Failed to delete product with ID: " + selectedProduct.getId());
                }
            }
        });


        contextMenu.getItems().addAll( editMenuItem , deleteMenuItem);


        productsTable.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();


            deleteButton.visibleProperty().addListener((obs, wasVisible, isNowVisible) -> {
                if (isNowVisible) {
                    row.getStyleClass().remove("highlighted-row");
                }
            });


            row.setOnMousePressed(event -> {

                if (isDeleteMode && !row.isEmpty() && event.getButton() == MouseButton.PRIMARY) {


                    if (row.getStyleClass().contains("highlighted-row")) {


                        row.getStyleClass().remove("highlighted-row");
                        productsTable.getSelectionModel().clearSelection(row.getIndex());

                    } else {


                        row.getStyleClass().add("highlighted-row");
                        productsTable.getSelectionModel().select(row.getIndex());
                    }

                       event.consume();
                }
            });

            row.setContextMenu(contextMenu);
            return row;
        });
    }
    private void updateFilter() {
        String nameQuery = nameSearchField.getText().trim().toLowerCase();
        String barcodeQuery = barcodeSearchField.getText().trim().toLowerCase();

        filteredData.setPredicate(product -> {

            if (nameQuery.isEmpty() && barcodeQuery.isEmpty()) {
                return true;
            }


            if (!nameQuery.isEmpty()) {
                boolean matchesName = product.getName() != null && !product.getName().isEmpty() &&
                        product.getName().toLowerCase().contains(nameQuery);
                return matchesName;
            }


            if (!barcodeQuery.isEmpty()) {
                boolean matchesBarcode = product.getBarcodes().stream()
                        .anyMatch(barcode -> barcode.toLowerCase().contains(barcodeQuery));
                return matchesBarcode;
            }


            return true;
        });
    }
    @FXML
    private void handleShowAddProductDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-product-dialog.fxml"));
            Parent page = loader.load();


            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Product");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(productsTable.getScene().getWindow());
            dialogStage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);


            AddProductDialogController controller = loader.getController();

            dialogStage.showAndWait();


            Product newProduct = controller.getNewProduct();


            if (newProduct != null) {

                productData.add(newProduct);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}