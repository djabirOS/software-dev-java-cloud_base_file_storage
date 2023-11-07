/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafrontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    public Button userButton;

    public void routeUser() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("usermanagement.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("User Management");
            stage.show();

            //close the current screen
            Stage currentScreen = (Stage) userButton.getScene().getWindow();
            currentScreen.close();
        } catch (Exception ex) {

        }
    }

    public void routeFile() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("filemanagement.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("File Management");
            stage.show();

            //close the current screen
            Stage currentScreen = (Stage) userButton.getScene().getWindow();
            currentScreen.close();
        } catch (Exception ex) {

        }
    }

    public void logout() {
        try {
            Store.email = "";
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();

            //close the current screen
            Stage currentScreen = (Stage) userButton.getScene().getWindow();
            currentScreen.close();
        } catch (Exception ex) {

        }
    }

}
