/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.javabackend;

//import com.mycompany.javabackend.Javabackend.GetHandler.PostHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Javabackend {

    public static HashMap<String, UserPermissions> permissionsMap = new HashMap<>();

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        permissionsMap = Helpers.loadPermissionsFromFile();
        if (permissionsMap == null) {
            permissionsMap = new HashMap<>();
        }

        // Define routes
        server.createContext("/login", new LoginHandler());
        server.createContext("/getUsers", new GetHandler());
        server.createContext("/addUser", new PostHandler());
        server.createContext("/editUser", new PutHandler());
        server.createContext("/deleteUser", new DeleteHandler());
        server.createContext("/createFile", new AddFileHandler());
        server.createContext("/deleteFile", new DeleteFileHandler());
        server.createContext("/getFiles", new GetFilesHandler());
        server.createContext("/shareFile", new ShareFileHandler());
        server.createContext("/uploadFile", new UploadFileHandler());
        server.createContext("/renameFile", new RenameFileHandler());
        server.createContext("/copyFile", new CopyFileHandler());
        server.createContext("/moveFile", new MoveFileHandler());
        // Add similar lines for PUT and DELETE

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port 8000");

    }

    static class GetHandler implements HttpHandler {

        private static final String FILE_PATH = "users.txt";

        @Override
        public void handle(HttpExchange t) throws IOException {
            // Your logic to handle GET request
            File file = new File(FILE_PATH);

            // Check if the file exists
            if (!file.exists()) {
                String response = "File not found";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            // Read the file into a JSONArray
            JSONArray usersArray = new JSONArray();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] userFields = line.split(",");
                    JSONObject userObject = new JSONObject();
                    userObject.put("name", userFields[0]);
                    userObject.put("email", userFields[1]);
                    userObject.put("password", userFields[2]); // Note: It may not be safe to return the password in a response
                    userObject.put("contact", userFields[3]);
                    userObject.put("address", userFields[4]);
                    usersArray.put(userObject);
                }
            }

            // Write the JSON response
            String response = usersArray.toString();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

    }

    static class LoginHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                // Read request body
                String requestBody = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                // Convert request body to JSON
                JSONObject json = new JSONObject(requestBody);
                String email = json.getString("email");
                String password = json.getString("password");

                // Read the users file
                Path usersPath = Paths.get("users.txt");
                List<String> users = Files.readAllLines(usersPath);

                // Check if the user exists
                for (String userLine : users) {
                    String[] parts = userLine.split(",");
                    if (parts[1].equals(email) && parts[2].equals(password)) {
                        // Login successful
                        JSONObject responseJson = new JSONObject();
                        responseJson.put("message", "Login successful.");
                        String response = responseJson.toString();
                        t.sendResponseHeaders(200, response.length());
                        OutputStream os = t.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                        return;
                    }
                }

                // Login failed
                JSONObject responseJson = new JSONObject();
                responseJson.put("message", "Email or password incorrect.");
                String response = responseJson.toString();
                t.sendResponseHeaders(401, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception ex) {
                System.out.println("Exception in login handler: " + ex);
                JSONObject responseJson = new JSONObject();
                responseJson.put("message", "Internal Server Error.");
                String response = responseJson.toString();
                t.sendResponseHeaders(500, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class PostHandler implements HttpHandler {

        private static final String FILE_PATH = "users.txt";

        @Override
        public void handle(HttpExchange t) throws IOException {
            // Read request body
            String requestBody = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            JSONObject json = new JSONObject(requestBody);
            String name = json.getString("name");
            String email = json.getString("email");
            String password = json.getString("password");
            String contact = json.getString("contact");
            String address = json.getString("address");

            // Check if email is already registered
            if (isEmailRegistered(email)) {
                String response = "Email is already registered";
                t.sendResponseHeaders(400, response.length()); // 400 Bad Request
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            // Add user to file
            addUserToFile(name, email, password, contact, address);

            String response = "User registered successfully";
            t.sendResponseHeaders(200, response.length()); // 200 OK
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private boolean isEmailRegistered(String email) throws IOException {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (String line : lines) {
                if (line.contains(email)) {
                    return true;
                }
            }
            return false;
        }

        private void addUserToFile(String name, String email, String password, String contact, String address) throws IOException {
            String user = name + "," + email + "," + password + "," + contact + "," + address;
            Files.write(Paths.get(FILE_PATH), (user + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        }
    }

    static class PutHandler implements HttpHandler {

        private static final String FILE_PATH = "users.txt";

        @Override
        public void handle(HttpExchange t) throws IOException {
            // Read request body
            String requestBody = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            JSONObject json = new JSONObject(requestBody);
            String emailToUpdate = json.getString("email");

            // Check if email is registered
            if (!isEmailRegistered(emailToUpdate)) {
                String response = "Email not found";
                t.sendResponseHeaders(404, response.length()); // 404 Not Found
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            // Update user in the file
            updateUserInFile(json);

            String response = "User updated successfully";
            t.sendResponseHeaders(200, response.length()); // 200 OK
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private boolean isEmailRegistered(String email) throws IOException {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (String line : lines) {
                if (line.contains(email)) {
                    return true;
                }
            }
            return false;
        }

        private void updateUserInFile(JSONObject user) throws IOException {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            List<String> updatedLines = lines.stream()
                    .map(line -> {
                        if (line.contains(user.getString("email"))) {
                            return user.getString("name") + "," + user.getString("email") + "," + user.getString("password")
                                    + "," + user.getString("contact") + "," + user.getString("address");
                        }
                        return line;
                    })
                    .collect(Collectors.toList());

            Files.write(Paths.get(FILE_PATH), updatedLines, StandardCharsets.UTF_8);
        }
    }

    static class UploadFileHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) {
            // Read the uploaded file bytes from the request
            try {
                InputStream is = t.getRequestBody();
                byte[] fileBytes = is.readAllBytes();

                // Generate a unique file name or use another strategy to name the file
                String uniqueFileName = "file_" + System.currentTimeMillis() + ".dat";
                Path filePath = Paths.get(uniqueFileName);

                // Write the file bytes to the file system
                Files.write(filePath, fileBytes);

                // Respond with a success message
                String response = "File uploaded successfully with name: " + uniqueFileName;
                t.sendResponseHeaders(200, response.length()); // 200 OK
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception ex) {
                System.out.println("exception ex " + ex);
            }
        }
    }

     static class DeleteHandler implements HttpHandler {

        private static final String FILE_PATH = "users.txt";

        @Override
        public void handle(HttpExchange t) {
            // Read request body
            try {
                String requestBody = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                JSONObject json = new JSONObject(requestBody);
                String emailToDelete = json.getString("email");

                // Check if email is registered
                if (!isEmailRegistered(emailToDelete)) {
                    String response = "Email not found";
                    t.sendResponseHeaders(404, response.length()); // 404 Not Found
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                    return;
                }

                // Delete user from the file
                deleteUserFromFile(emailToDelete);

                String response = "User deleted successfully";
                t.sendResponseHeaders(200, response.length()); // 200 OK
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception ex) {
                System.out.println("Exception " + ex);

            }
        }

        private boolean isEmailRegistered(String email) throws IOException {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (String line : lines) {
                if (line.contains(email)) {
                    return true;
                }
            }
            return false;
        }

        private void deleteUserFromFile(String email) throws IOException {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.contains(email))
                    .collect(Collectors.toList());

            Files.write(Paths.get(FILE_PATH), updatedLines, StandardCharsets.UTF_8);
        }
    }

    static class AddFileHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) {
            try {
                // Read request body
                String requestBody = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                JSONObject json = new JSONObject(requestBody);
                String content = json.getString("content");
                String userEmail = json.getString("email");

                UserPermissions permission = new UserPermissions();
                permission.delete = true;
                permission.edit = true;
                permission.read = true;
                permission.copy = true;
                permission.move = true;

                // Generate unique file name
                String uniqueFileName = "file_" + System.currentTimeMillis() + ".txt";
                permission.fileName = uniqueFileName;
                permissionsMap.put(userEmail, permission);
                Helpers.savePermissionsToFile(permissionsMap);

                // Path to save the file
                Path filePath = Paths.get(uniqueFileName);

                // Write content to the file
                Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));

                //response
                String response = "File created successfully with name: " + uniqueFileName;
                t.sendResponseHeaders(201, response.length()); // 201 Created
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception ex) {
                System.out.println("Exception in  add file handler : " + ex);
            }
        }
    }

    static class DeleteFileHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            // Read request body
            String requestBody = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            JSONObject json = new JSONObject(requestBody);
            String fileName = json.getString("fileName");
            String userEmail = json.getString("userEmail");

            // Path to the file
            Path filePath = Paths.get(fileName);

            // Response message
            String response;

            if (Files.exists(filePath)) {
                // Delete the file
                UserPermissions permissions = permissionsMap.get(userEmail);
                if (permissions != null && permissions.fileName.equals(fileName) && permissions.edit) {
                    Files.delete(filePath);
                    response = "File deleted successfully.";
                    t.sendResponseHeaders(200, response.length()); // 200 OK
                } else {
                    response = "User doesnot have access to delete the file";
                    t.sendResponseHeaders(400, response.length()); // 400 Not OK  
                }

            } else {
                response = "File not found.";
                t.sendResponseHeaders(404, response.length()); // 404 Not Found
            }

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class GetFilesHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String pathToFiles = System.getProperty("user.dir"); // Gets the current working directory
            File folder = new File(pathToFiles);
            File[] listOfFiles = folder.listFiles();

            JSONArray filesArray = new JSONArray();

            for (File file : listOfFiles) {
                if (file.isFile() && !file.getName().equals("users.txt") && !file.getName().equals("pom.xml") && !file.getName().equals(".DS_Store") && !file.getName().equals("permissions.ser")) { // Check if the file has the .txt extension
                    JSONObject fileObject = new JSONObject();
                    fileObject.put("name", file.getName());
                    fileObject.put("size", file.length());
                    filesArray.put(fileObject);
                }
            }

            String response = filesArray.toString();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class ShareFileHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            // Read request body
            String requestBody = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            JSONObject json = new JSONObject(requestBody);
            String ownerEmail = json.getString("ownerEmail");
            String sharedWithEmail = json.getString("sharedWithEmail");
            String fileName = json.getString("fileName");

            UserPermissions ownerPermission = permissionsMap.get(ownerEmail);

            // Check if the owner of the file is the one sharing it
            if (ownerPermission != null && ownerPermission.fileName.equals(fileName)) {
                UserPermissions sharedPermission = new UserPermissions();
                sharedPermission.delete = true; // Shared user can delete
                sharedPermission.edit = true; // Shared user can edit
                sharedPermission.read = true; // Shared user can read
                sharedPermission.copy = true;
                sharedPermission.move = true;
                sharedPermission.fileName = fileName;

                permissionsMap.put(sharedWithEmail, sharedPermission);

                String response = "File shared successfully with " + sharedWithEmail;
                t.sendResponseHeaders(200, response.length()); // 200 OK
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                String response = "Unauthorized request";
                t.sendResponseHeaders(403, response.length()); // 403 Forbidden
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class RenameFileHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            // Read request body
            String requestBody = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            JSONObject json = new JSONObject(requestBody);
            String currentFileName = json.getString("currentFileName");
            String newFileName = json.getString("newFileName");
            String userEmail = json.getString("userEmail");

            // Path to the file
            Path currentFilePath = Paths.get(currentFileName);
            Path newFilePath = Paths.get(newFileName);

            System.out.println("Current file path : " + currentFilePath);
            System.out.println("Current file name : " + currentFileName);

            System.out.println("New file path : " + newFilePath);
            System.out.println("New file name : " + newFileName);

            // Response message
            String response;

            // Check permissions (replace with your actual permission check)
            UserPermissions permissions = permissionsMap.get(userEmail);
            if (permissions != null && permissions.fileName.equals(currentFileName) && permissions.edit) {
                if (Files.exists(currentFilePath)) {
                    // Rename the file
                    Files.move(currentFilePath, newFilePath);
                    response = "File renamed successfully.";
                    t.sendResponseHeaders(200, response.length()); // 200 OK
                } else {
                    response = "Current file not found.";
                    t.sendResponseHeaders(404, response.length()); // 404 Not Found
                }
            } else {
                response = "User does not have access to rename the file.";
                t.sendResponseHeaders(403, response.length()); // 403 Forbidden
            }

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class MoveFileHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            // Read request body
            String requestBody = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            JSONObject json = new JSONObject(requestBody);
            String sourceFileName = json.getString("sourceFileName");
            String destinationPath = json.getString("destinationPath");
            String userEmail = json.getString("userEmail");

            Path sourceFilePath = Paths.get(sourceFileName);
            Path targetFilePath = Paths.get(destinationPath, sourceFileName);

            // Response message
            String response;

            if (Files.exists(sourceFilePath) && hasPermissionToMove(userEmail, sourceFileName)) {
                Files.move(sourceFilePath, targetFilePath);
                response = "File moved successfully.";
                t.sendResponseHeaders(200, response.length()); // 200 OK
            } else {
                response = "Not authenticated to move the file.";
                t.sendResponseHeaders(400, response.length()); // 400 Bad Request
            }

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

// You'll need to implement a method to check permissions for the user
    private static boolean hasPermissionToMove(String userEmail, String fileName) {
       UserPermissions per = permissionsMap.get(userEmail);
        return per.fileName.equals(fileName) && per.move;
    }

    static class CopyFileHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            // Read request body
            String requestBody = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            JSONObject json = new JSONObject(requestBody);
            String sourceFileName = json.getString("sourceFileName");
            String destinationPath = json.getString("destinationPath");
            String userEmail = json.getString("userEmail");

            Path sourceFilePath = Paths.get(sourceFileName);
            Path targetFilePath = Paths.get(destinationPath, sourceFileName);

            // Response message
            String response;

            if (Files.exists(sourceFilePath) && hasPermissionToCopy(userEmail, sourceFileName)) {
                Files.copy(sourceFilePath, targetFilePath);
                response = "File copied successfully.";
                t.sendResponseHeaders(200, response.length()); // 200
            } else {
                response = "Not authenticated to copy the file";
                t.sendResponseHeaders(400, response.length()); // 400 Bad Request
            }

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

// You'll need to implement a method to check permissions for the user
    private static boolean hasPermissionToCopy(String userEmail, String fileName) {
        // Check permissions for the user here
        UserPermissions per = permissionsMap.get(userEmail);
        return per.fileName.equals(fileName) && per.copy;
    }

}
