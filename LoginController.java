/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafrontend;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;

    @FXML
    public void initialize() {

    }

    public void routeRegister(Event e) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("signup.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Signup");
        stage.show();

        //close the current screen
        Stage currentScreen = (Stage) loginButton.getScene().getWindow();
        currentScreen.close();

        
    }

    public void routeLogin(Event e) throws Exception {
        String userName = usernameField.getText();
        String password = passwordField.getText();

        String response = UserRoutes.loginUser(userName, password);
        if (response.equals("")) {
            Store.email = userName;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();

            //close the current screen
            Stage currentScreen = (Stage) loginButton.getScene().getWindow();
            currentScreen.close();
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
