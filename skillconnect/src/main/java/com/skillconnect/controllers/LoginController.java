package com.skillconnect.controllers;

import com.jfoenix.controls.*;
import com.skillconnect.models.User;
import com.skillconnect.utils.PasswordUtils;
import com.skillconnect.utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.io.InputStream;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML private VBox loginForm;
    @FXML private VBox registerForm;

    @FXML private JFXTextField loginUsername;
    @FXML private JFXPasswordField loginPassword;
    @FXML private JFXRadioButton loginAdminRadio;
    @FXML private JFXRadioButton loginVolunteerRadio;

    @FXML private JFXTextField registerUsername;
    @FXML private JFXPasswordField registerPassword;
    @FXML private JFXPasswordField confirmPassword;
    @FXML private JFXRadioButton registerAdminRadio;
    @FXML private JFXRadioButton registerVolunteerRadio;

    @FXML
    private ImageView loginIllustration;

    private ToggleGroup loginRoleGroup;
    private ToggleGroup registerRoleGroup;

    @FXML
    private void initialize() {
        // Initialize role toggle groups
        loginRoleGroup = new ToggleGroup();
        loginAdminRadio.setToggleGroup(loginRoleGroup);
        loginVolunteerRadio.setToggleGroup(loginRoleGroup);
        loginAdminRadio.setSelected(true);

        registerRoleGroup = new ToggleGroup();
        registerAdminRadio.setToggleGroup(registerRoleGroup);
        registerVolunteerRadio.setToggleGroup(registerRoleGroup);
        registerAdminRadio.setSelected(true);

        // Load the image with debug information
        try {
            System.out.println("Attempting to load image...");
            String imagePath = "/images/login-illustration.png";
            System.out.println("Image path: " + imagePath);

            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                System.out.println("Image stream found successfully");
                Image image = new Image(imageStream);
                loginIllustration.setImage(image);
                System.out.println("Image set to ImageView");
            } else {
                System.out.println("Image stream is null - file not found");
                // Try alternative path
                String altPath = "file:src/main/resources/images/login-illustration.png";
                System.out.println("Trying alternative path: " + altPath);
                Image image = new Image(altPath);
                loginIllustration.setImage(image);
                System.out.println("Image set using alternative path");
            }
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        try {
            String username = loginUsername.getText();
            String password = loginPassword.getText();
            String role = loginRoleGroup.getSelectedToggle() == loginAdminRadio ? "ADMIN" : "VOLUNTEER";

            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please enter both username and password");
                return;
            }

            User user = User.authenticate(username, password, role);
            if (user != null) {
                // Set the user in SessionManager first
                SessionManager.getInstance().setCurrentUser(user);
                System.out.println("User authenticated and session set: " + user.getUsername());
                loadDashboard(role, user);
            } else {
                showAlert("Error", "Invalid credentials");
            }
        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        try {
            String username = registerUsername.getText();
            String password = registerPassword.getText();
            String confirmPass = confirmPassword.getText();
            String role = registerRoleGroup.getSelectedToggle() == registerAdminRadio ? "ADMIN" : "VOLUNTEER";

            if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                showAlert("Error", "Please fill in all fields");
                return;
            }

            if (!password.equals(confirmPass)) {
                showAlert("Error", "Passwords do not match");
                return;
            }

            if (!PasswordUtils.isValidPassword(password)) {
                showAlert("Error", "Password must contain:\n" +
                        "• At least 8 characters\n" +
                        "• At least one uppercase letter\n" +
                        "• At least one lowercase letter\n" +
                        "• At least one number\n" +
                        "• At least one special character (!@#$%^&* etc.)");
                return;
            }

            if (User.usernameExists(username)) {
                showAlert("Error", "Username already exists");
                return;
            }

            User.register(username, password, role);
            showAlert("Success", "Registration successful! Please login.");
            switchToLogin();
        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void switchToRegister() {
        animateFormTransition(loginForm, registerForm);
    }

    @FXML
    private void switchToLogin() {
        animateFormTransition(registerForm, loginForm);
    }

    private void animateFormTransition(VBox fromForm, VBox toForm) {
        // Fade out current form
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), fromForm);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            fromForm.setVisible(false);
            toForm.setVisible(true);

            // Fade in new form
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toForm);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadDashboard(String role, User authenticatedUser) {
        try {
            String fxmlFile = role.equals("ADMIN") ?
                "/fxml/AdminDashboard.fxml" : "/fxml/VolunteerDashboard.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Set the current user in the dashboard controller
            DashboardController controller = loader.getController();
            controller.setUser(authenticatedUser);

            Stage stage = (Stage) loginForm.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load dashboard");
            e.printStackTrace();
        }
    }
}