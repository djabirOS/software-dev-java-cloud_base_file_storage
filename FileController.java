/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafrontend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Files;
import com.mycompany.javafrontend.FilesObject;
import java.io.File;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileController {

    @FXML
    public TableView tableView;

    @FXML
    public TableColumn<FilesObject, String> fileNameColumn;
    @FXML
    public TableColumn<FilesObject, String> fileSizeColumn;

    @FXML
    public Button newFile;
    @FXML
    public Button uploadFile;

    private ObservableList<FilesObject> fileList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        updateFileList();

        fileNameColumn.setCellValueFactory(cellData -> (new SimpleStringProperty(cellData.getValue().name)));
        fileSizeColumn.setCellValueFactory(cellData -> (new SimpleStringProperty(String.valueOf(cellData.getValue().size))));
        tableView.setItems(fileList);

    }

    private void updateFileList() {
        fileList.clear();
        List<FilesObject> files = FileRoutes.getAllFiles();
        if (!files.isEmpty()) {
            System.out.println("files are not null");
            System.out.println("extracted files : " + files);
            fileList.addAll(files);
            System.out.println("fileList : " + fileList.toString());

        } else {
            System.out.println("files are null");
        }

        tableView.setItems(fileList);

    }

    public void createNewFile() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New File");
        dialog.setHeaderText("Enter the content for the new file:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(content -> {
            System.out.println("Content entered: " + content);
            // Send the content to the server, for example:
            // createFileOnServer(content);
            // Update the file list if needed
            String response = FileRoutes.createFileOnServer(content, Store.email);
            showToast(response);
            updateFileList();
        });
    }

    public void deleteFile() {
        FilesObject selectedFile = (FilesObject) tableView.getSelectionModel().getSelectedItem();

        if (selectedFile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No File Selected");
            alert.setContentText("Please select a file to delete.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete File");
        alert.setHeaderText("Are you sure you want to delete the file?");
        alert.setContentText("File Name: " + selectedFile.name);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String fileName = selectedFile.name;
            System.out.println("File to delete: " + fileName);
            String response = FileRoutes.deleteFile(Store.email, fileName);
            showToast(response);
            updateFileList();
        }
    }

    public void renameFile() {
        // Check if a file is selected
        FilesObject selectedFile = (FilesObject) tableView.getSelectionModel().getSelectedItem();
        if (selectedFile == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a file to rename.");
            alert.showAndWait();
            return;
        }

        String currentFileName = selectedFile.name;

        // Create an alert with custom content
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Rename File");
        alert.setHeaderText("Renaming file: " + currentFileName);

        // Add text field for new name
        TextField textField = new TextField();
        textField.setPromptText("Enter new name");

        // Add content to the alert
        GridPane grid = new GridPane();
        grid.add(new Label("New name:"), 0, 0);
        grid.add(textField, 1, 0);
        alert.getDialogPane().setContent(grid);

        // Get the user's input
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String newName = textField.getText();
            if (newName != null && !newName.trim().isEmpty()) {
                String renameSuccessful = FileRoutes.renameFile(Store.email, currentFileName, newName);
                if (renameSuccessful.equals("")) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setContentText("File renamed successfully!");
                    successAlert.showAndWait();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setContentText(renameSuccessful);
                    errorAlert.showAndWait();
                }
            } else {
                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setContentText("You must enter a new name for the file.");
                warningAlert.showAndWait();
            }
        }

        updateFileList();
    }

    public void uploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");

        // Set extension filter if needed
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(tableView.getScene().getWindow()); // get window from table view
        if (file != null) {
            Path path = file.toPath();
            System.out.println("Selected file path: " + path.toString());
            String response = FileRoutes.uploadFile(path);
            showToast(response);

        }

        updateFileList();
    }

    public void moveFile() {
        FilesObject selectedFile = (FilesObject) tableView.getSelectionModel().getSelectedItem();
        if (selectedFile == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a file to move.");
            alert.showAndWait();
            return;
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Destination Directory");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            String destinationPath = selectedDirectory.getAbsolutePath();
            String moveSuccessful = FileRoutes.moveFile(Store.email,selectedFile.name, destinationPath);
            if (moveSuccessful.equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("File moved successfully!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(moveSuccessful);
                alert.showAndWait();
            }
        }
    }

    public void copyFile() {
        FilesObject selectedFile = (FilesObject) tableView.getSelectionModel().getSelectedItem();
        if (selectedFile == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a file to copy.");
            alert.showAndWait();
            return;
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Destination Directory");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            String destinationPath = selectedDirectory.getAbsolutePath();
            String copySuccessful = FileRoutes.copyFile(Store.email,selectedFile.name, destinationPath);
            if (copySuccessful.equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("File copied successfully!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(copySuccessful);
                alert.showAndWait();
            }
        }
    }

    public void home() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("File Management");
            stage.show();

            //close the current screen
            Stage currentScreen = (Stage) newFile.getScene().getWindow();
            currentScreen.close();
        } catch (Exception ex) {

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
