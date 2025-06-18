package com.example.demo3;

import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.time.LocalDate;
import java.util.List;

public class Product {

    private final IntegerProperty id;
    private final ListProperty<String> barcodes;
    private final StringProperty name;
    private final IntegerProperty quantity;
    private final DoubleProperty buyingPrice;
    private final ObjectProperty<LocalDate> expiryDate;
    private final DoubleProperty unitPrice;
    private final DoubleProperty price2;
    private final DoubleProperty price3;


    public Product(int id, List<String> barcodes, String name, int quantity, double buyingPrice, LocalDate expiryDate, double unitPrice, double price2, double price3) {
        this.id = new SimpleIntegerProperty(id);
        this.barcodes = new SimpleListProperty<>(FXCollections.observableArrayList(barcodes)); // Properly initialize the barcodes
        this.name = new SimpleStringProperty(name);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.buyingPrice = new SimpleDoubleProperty(buyingPrice);
        this.expiryDate = new SimpleObjectProperty<>(expiryDate);
        this.unitPrice = new SimpleDoubleProperty(unitPrice);
        this.price2 = new SimpleDoubleProperty(price2);
        this.price3 = new SimpleDoubleProperty(price3);
    }


    public void setId(int id) {
        this.id.set(id);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public List<String> getBarcodes() {
        return barcodes.get();
    }

    public ListProperty<String> barcodesProperty() {
        return barcodes;
    }


    public void setBarcodes(List<String> barcodes) {
        this.barcodes.set(FXCollections.observableArrayList(barcodes));
    }


    public String getMainBarcode() {
        return (barcodes != null && !barcodes.isEmpty()) ? barcodes.get(0) : "";
    }

    public StringProperty mainBarcodeProperty() {
        return new SimpleStringProperty(getMainBarcode());
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public double getBuyingPrice() {
        return buyingPrice.get();
    }

    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice.set(buyingPrice);
    }

    public DoubleProperty buyingPriceProperty() {
        return buyingPrice;
    }

    public LocalDate getExpiryDate() {
        return expiryDate.get();
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate.set(expiryDate);
    }

    public ObjectProperty<LocalDate> expiryDateProperty() {
        return expiryDate;
    }

    public double getUnitPrice() {
        return unitPrice.get();
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice.set(unitPrice);
    }

    public DoubleProperty unitPriceProperty() {
        return unitPrice;
    }

    public double getPrice2() {
        return price2.get();
    }

    public void setPrice2(double price2) {
        this.price2.set(price2);
    }

    public DoubleProperty price2Property() {
        return price2;
    }

    public double getPrice3() {
        return price3.get();
    }

    public void setPrice3(double price3) {
        this.price3.set(price3);
    }

    public DoubleProperty price3Property() {
        return price3;
    }

    public void addBarcode(String barcode) {
        if (!barcodes.contains(barcode)) {
            barcodes.add(barcode);
        }
    }


    public void removeBarcode(String barcode) {
        barcodes.remove(barcode);
    }


    public boolean hasBarcodes() {
        return !barcodes.isEmpty();
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id.get() +
                ", barcodes=" + barcodes.get() +
                ", name='" + name.get() + '\'' +
                ", quantity=" + quantity.get() +
                ", buyingPrice=" + buyingPrice.get() +
                ", expiryDate=" + expiryDate.get() +
                ", unitPrice=" + unitPrice.get() +
                ", price2=" + price2.get() +
                ", price3=" + price3.get() +
                '}';
    }
}