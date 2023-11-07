/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafrontend;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField contactField;
    @FXML
    private TextArea addressField;
    @FXML
    private TextField emailField;

    @FXML
    private void initialize() {

    }

    public void register() {
        String name = nameField.getText();
        String password = passwordField.getText();
        String contact = contactField.getText();
        String address = addressField.getText();
        String email = emailField.getText();

        String response = UserRoutes.registerUser(name, email, password, contact, address);
        if (response.equals("")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));

                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("File Management");
                stage.show();

                //close the current screen
                Stage currentScreen = (Stage) emailField.getScene().getWindow();
                currentScreen.close();
            } catch (Exception ex) {

            }

        } else {
            showToast(response);
        }
    }

    public void showToast(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Response");
        alert.setHeaderText(message);
//            alert.setContentText("Please select a User to edit.");
        alert.showAndWait();
    }

}
