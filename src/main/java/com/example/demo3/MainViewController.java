package com.example.demo3;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainViewController {
    @FXML private BorderPane mainPane;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Button userButton;


    @FXML
    private void initialize() {
        setupClock();

    }
    @FXML
    void handleShowProducts(ActionEvent event) {
        loadView("/com/example/demo3/products-view.fxml");
    }

    @FXML
    void handleShowDashboard(ActionEvent event) {
        loadView("/com/example/demo3/main-view.fxml");
    }
    private void loadView(String fxmlPath) {
        try {
            System.out.println("Loading FXML: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                System.err.println("FXML file not found: " + fxmlPath);
                return;
            }
            Parent view = loader.load();
            mainPane.setCenter(view);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initData(User user) {
        if (user != null) {
            welcomeLabel.setText("Welcome back, " + user.getUsername() + "!");
            userButton.setText(user.getUsername());
        }
    }

    private void setupClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            dateLabel.setText(LocalDateTime.now().format(dateFormatter));
            timeLabel.setText(LocalDateTime.now().format(timeFormatter));
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
}
