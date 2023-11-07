/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafrontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class UserController {

    @FXML
    public TableView tableView;

    @FXML
    public TableColumn<User, String> serialNumberColumn;
    @FXML
    public TableColumn<User, String> nameColumn;
    @FXML
    public TableColumn<User, String> emailColumn;
    @FXML
    public TableColumn<User, String> contactColumn;
    @FXML
    public TableColumn<User, String> addressColumn;

    @FXML
    public TextField addName;
    @FXML
    public TextField addEmail;
    @FXML
    public PasswordField addPassword;

    @FXML
    public PasswordField confirmPassword;

    @FXML
    public TextArea addAddress;

    @FXML
    public TextField addContact;
    @FXML
    public RadioButton addRadioButton;

    @FXML
    public RadioButton editRadioButton;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        updateUserList();

        ToggleGroup group = new ToggleGroup();
        addRadioButton.setToggleGroup(group);
        editRadioButton.setToggleGroup(group);

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == editRadioButton) {
                addEmail.setDisable(true);
                addPassword.setDisable(true);
                confirmPassword.setDisable(true);
                
            } else {
                addEmail.setDisable(false);
                addPassword.setDisable(false);
                confirmPassword.setDisable(false);
            }
        });

        nameColumn.setCellValueFactory(cellData -> (new SimpleStringProperty(cellData.getValue().getName())));
        emailColumn.setCellValueFactory(cellData -> (new SimpleStringProperty(cellData.getValue().getEmail())));
        contactColumn.setCellValueFactory(cellData -> (new SimpleStringProperty(cellData.getValue().getContact())));
        addressColumn.setCellValueFactory(cellData -> (new SimpleStringProperty(cellData.getValue().getAddress())));
        tableView.setItems(userList);

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                User selectedUser = (User) newValue;
                addName.setText(selectedUser.getName());
                addEmail.setText(selectedUser.getEmail());
                addPassword.setText(selectedUser.getPassword());
                addContact.setText(selectedUser.getContact());
                addAddress.setText(selectedUser.getAddress());
            } else {
                // Optionally, clear the text fields if no item is selected
                addName.setText("");
                addEmail.setText("");
                addPassword.setText("");
                addContact.setText("");
                addAddress.setText("");
            }
        });

    }

    private void updateUserList() {
        userList.clear();
        List<User> users = Routes.getUsers();
        if (!users.isEmpty()) {
            System.out.println("users are not null");
            System.out.println("extracted users : " + users);
            userList.addAll(users);
            System.out.println("userList : " + userList.toString());

        } else {
            System.out.println("users are null");
        }

        tableView.setItems(userList);

    }

    private User getValuesFromTextField() {
        String name = addName.getText();
        String email = addEmail.getText();
        String password = addPassword.getText();
        String address = addAddress.getText();
        String contact = addContact.getText();

        return new User("", name, email, password, contact, address);
    }

    public void addUser() {
        User user = getValuesFromTextField();
        String response = Routes.addUser(user);
        showToast(response);
        updateUserList();

    }

    public void editUser() {
        User selectedUser = (User) tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No User Selected");
            alert.setContentText("Please select a User to edit.");
            alert.showAndWait();
            return;
        }

        User editedUser = getValuesFromTextField();
        editedUser.setId(selectedUser.getId()); // Assuming there's an ID field and setter
        String response = Routes.editUser(editedUser); // Assuming there's an editUser method in Routes
        showToast(response);
        updateUserList();
    }

    public void deleteUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete User");
        dialog.setHeaderText("Enter the email of the user you want to delete:");
        dialog.setContentText("Email:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(email -> {
            String response = Routes.deleteUser(email);
            showToast(response);

        });
        updateUserList();
    }

    public void home() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();

            //close the current screen
            Stage currentScreen = (Stage) addName.getScene().getWindow();
            currentScreen.close();

        } catch (Exception ex) {
            System.out.println(ex);
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
