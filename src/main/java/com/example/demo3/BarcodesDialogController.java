package com.example.demo3;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BarcodesDialogController {

    @FXML private VBox barcodesContainer;
    private List<String> barcodes = new ArrayList<>();
    private int barcodeCounter = 1;

    @FXML
    public void initialize() {

        addNewBarcodeField();

        barcodesContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                adjustWindowHeight();
            }
        });
    }
    public void initializeWithBarcodes(List<String> initialBarcodes) {
        this.barcodes.clear();
        this.barcodes.addAll(initialBarcodes);

        barcodesContainer.getChildren().clear();
        for (String barcode : initialBarcodes) {
            addBarcodeFieldWithValue(barcode);
        }

        if (initialBarcodes.isEmpty()) {
            addNewBarcodeField();
        }
    }
    private void addBarcodeFieldWithValue(String barcodeValue) {
        HBox newRow = new HBox(10);
        newRow.setPadding(new Insets(0, 0, 10, 0));
        TextField barcodeField = new TextField(barcodeValue);
        barcodeField.setPromptText("Barcode " + barcodeCounter++);
        barcodeField.getStyleClass().add("dialog-field");
        HBox.setHgrow(barcodeField, javafx.scene.layout.Priority.ALWAYS);

        Button removeButton = new Button("✕");
        removeButton.getStyleClass().add("remove-barcode-btn");

        removeButton.setOnAction(event -> {
            barcodesContainer.getChildren().remove(newRow);
            updateBarcodePrompts();
            adjustWindowHeight();
        });

        newRow.getChildren().addAll(barcodeField, removeButton);
        barcodesContainer.getChildren().add(newRow);


        Platform.runLater(this::adjustWindowHeight);
    }

    @FXML
    private void addNewBarcodeField() {
        HBox newRow = new HBox(10);
        newRow.setPadding(new Insets(0, 0, 10, 0));
        TextField barcodeField = new TextField();

        barcodeField.setPromptText("Barcode " + barcodeCounter++);
        barcodeField.getStyleClass().add("dialog-field");
        HBox.setHgrow(barcodeField, javafx.scene.layout.Priority.ALWAYS);

        Button removeButton = new Button("✕");
        removeButton.getStyleClass().add("remove-barcode-btn");


        removeButton.setOnAction(event -> {
            barcodesContainer.getChildren().remove(newRow);
            updateBarcodePrompts();
            adjustWindowHeight();
        });

        newRow.getChildren().addAll(barcodeField, removeButton);
        barcodesContainer.getChildren().add(newRow);


        Platform.runLater(this::adjustWindowHeight);
    }

    private void updateBarcodePrompts() {
        barcodeCounter = 1;
        for (javafx.scene.Node node : barcodesContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                for (javafx.scene.Node child : hbox.getChildren()) {
                    if (child instanceof TextField) {
                        ((TextField) child).setPromptText("Barcode " + barcodeCounter++);
                    }
                }
            }
        }
    }


    private void adjustWindowHeight() {

        double baseHeight = calculateBaseHeight();
        double rowHeight = 60;
        double totalHeight = baseHeight + (barcodesContainer.getChildren().size() * rowHeight);


        Stage stage = (Stage) barcodesContainer.getScene().getWindow();
        if (stage != null) {
            stage.setHeight(totalHeight);
            stage.setMinHeight(totalHeight);
            stage.setMaxHeight(totalHeight + 100);
        }
    }

    private double calculateBaseHeight() {

        double titleHeight = 50;
        double buttonsHeight = 100;
        double padding = 50;

        return titleHeight + buttonsHeight + padding;
    }

    @FXML
    private void handleApplyAction() {
        this.barcodes = barcodesContainer.getChildren().stream()
                .filter(node -> node instanceof HBox)
                .map(node -> (HBox) node)
                .flatMap(hbox -> hbox.getChildren().stream())
                .filter(node -> node instanceof TextField)
                .map(node -> ((TextField) node).getText())
                .filter(text -> text != null && !text.trim().isEmpty())
                .collect(Collectors.toList());

        closeDialog();
    }

    @FXML
    private void handleCancelAction() {
        this.barcodes.clear();
        closeDialog();
    }


    public List<String> getBarcodes() {
        return barcodesContainer.getChildren().stream()
                .filter(node -> node instanceof HBox)
                .map(node -> (HBox) node)
                .flatMap(hbox -> hbox.getChildren().stream())
                .filter(node -> node instanceof TextField)
                .map(node -> ((TextField) node).getText())
                .filter(text -> text != null && !text.trim().isEmpty())
                .collect(Collectors.toList());
    }


    private void closeDialog() {
        Stage stage = (Stage) barcodesContainer.getScene().getWindow();
        stage.close();
    }
}