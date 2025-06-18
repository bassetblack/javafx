package com.example.demo3;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private VBox formContainer;
    @FXML private HBox topBar;

    private boolean isPasswordVisible = false;

    @FXML
    protected void initialize() {
        progressIndicator.setVisible(false);
        passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());
        makeWindowDraggable();
    }

    @FXML
    protected void handleLogin() {
        showProgress(true);
        statusLabel.setText("");

        Task<Optional<User>> authTask = new Task<>() {
            @Override
            protected Optional<User> call() {
                return DatabaseUtil.authenticateUser(usernameField.getText(), passwordField.getText());
            }
        };

        authTask.setOnSucceeded(event -> {
            showProgress(false);
            authTask.getValue().ifPresentOrElse(
                    this::loadMainApplication,
                    () -> {
                        statusLabel.setText("Invalid username or password");
                        statusLabel.getStyleClass().setAll("status-label", "error");
                    }
            );
        });

        authTask.setOnFailed(event -> {
            showProgress(false);
            statusLabel.setText("Database error. Please try again.");
            statusLabel.getStyleClass().setAll("status-label", "error");
            authTask.getException().printStackTrace();
        });

        new Thread(authTask).start();
    }


    private void loadMainApplication(User user) {
        try {

            Stage loginStage = (Stage) formContainer.getScene().getWindow();
            loginStage.close();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = loader.load();

            MainViewController controller = loader.getController();
            controller.initData(user);

            Stage mainStage = new Stage();
            mainStage.setTitle("Dashboard");
            mainStage.setScene(new Scene(root));
            mainStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load application window.");
            statusLabel.getStyleClass().setAll("status-label", "error");
        }
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisible(show);
        formContainer.setVisible(!show);
    }

    private void makeWindowDraggable() {
        topBar.setOnMousePressed(event -> {
            Stage stage = (Stage) topBar.getScene().getWindow();
            double xOffset = stage.getX() - event.getScreenX();
            double yOffset = stage.getY() - event.getScreenY();
            topBar.setUserData(new double[]{xOffset, yOffset});
        });

        topBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) topBar.getScene().getWindow();
            double[] offset = (double[]) topBar.getUserData();
            stage.setX(event.getScreenX() + offset[0]);
            stage.setY(event.getScreenY() + offset[1]);
        });
    }

    @FXML
    protected void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        passwordField.setVisible(!isPasswordVisible);
        passwordTextField.setVisible(isPasswordVisible);
    }

    @FXML
    protected void minimizeWindow() {
        ((Stage) topBar.getScene().getWindow()).setIconified(true);
    }

    @FXML
    protected void closeWindow() {
        ((Stage) topBar.getScene().getWindow()).close();
    }
}
