package com.example.demo3;

import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;

public class BarcodeTableCell extends javafx.scene.control.TableCell<Product, String> {

    private final Popup popup = new Popup();
    private final VBox popupContent = new VBox();

    private static Popup currentlyOpenPopup = null;

    public BarcodeTableCell() {

        popupContent.setStyle("-fx-background-color: #343a40; -fx-padding: 10px; -fx-border-color: black; -fx-border-width: 1px; -fx-text-fill: white;");
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {

            setText(item);

            popupContent.getChildren().clear();

            Product product = getTableView().getItems().get(getIndex());

            for (String barcode : product.getBarcodes()) {
                Label label = new Label(barcode);
                label.setStyle("-fx-font-size: 14px; -fx-padding: 5px; -fx-text-fill: white;");
                popupContent.getChildren().add(label);
            }

            setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    if (!popup.isShowing()) {

                        if (currentlyOpenPopup != null && currentlyOpenPopup.isShowing()) {
                            currentlyOpenPopup.hide();
                        }

                        popup.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);

                        popup.getContent().clear();
                        popup.getContent().add(popupContent);

                        double screenWidth = localToScreen(0, getHeight()).getX();
                        double screenHeight = localToScreen(0, getHeight()).getY();

                        popup.show(this, screenWidth, screenHeight);

                        currentlyOpenPopup = popup;

                        if (getScene() != null) {
                            getScene().setOnMouseClicked(sceneEvent -> {

                                if (!getTableBoundsInScreen().contains(sceneEvent.getScreenX(), sceneEvent.getScreenY())) {
                                    if (popup.isShowing()) {
                                        popup.hide();
                                        currentlyOpenPopup = null;
                                    }
                                }
                            });
                        }
                    } else {
                        popup.hide();
                        currentlyOpenPopup = null;
                    }
                }
            });
        }
    }


    private Bounds getTableBoundsInScreen() {
        return getTableView().localToScreen(getTableView().getBoundsInLocal());
    }
}