/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafrontend;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileRoutes {

    public static List<FilesObject> getAllFiles() {
        List<FilesObject> files = new ArrayList<>();
        try {
            // Create an HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create a GET request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/getFiles")) // Adjust the URL as needed
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Success response
                String responseBody = response.body();

                // Use Gson to deserialize the JSON response
                Gson gson = new Gson();
                Type listType = new TypeToken<List<FilesObject>>() {
                }.getType();
                files = gson.fromJson(responseBody, listType);
            } else {
                // Handle error responses
                System.out.println("Error fetching files. Server responded with status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while fetching files.");
        }
        System.out.println("files: " + files.toString());
        return files;
    }

    public static String uploadFile(Path filePath) {
        try {
            // Create an HttpClient
            HttpClient client = HttpClient.newBuilder().build();

            // Read the file as bytes
            byte[] fileBytes = Files.readAllBytes(filePath);

            // Create a POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/uploadFile")) // Adjust the URL as needed
                    .POST(HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                    .header("Content-Type", "application/octet-stream")
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Success response
                return "File uploaded successfully.";
            } else {
                // Handle error response
                return "Error uploading file. Server responded with status code: " + response.statusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while uploading the file.";
        }
    }

    public static String createFileOnServer(String content, String userEmail) {
        String returnString = "";
        try {
            // Convert content and userEmail to JSON using Gson
            Gson gson = new Gson();
            JsonObject json = new JsonObject();
            json.addProperty("content", content);
            json.addProperty("email", userEmail);
            String jsonString = gson.toJson(json);

            // Create an HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create a POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/createFile")) // Adjust the URL as needed
                    .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                    .header("Content-Type", "application/json")
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            if (statusCode == 201) {
                // Success response
                returnString = "File created successfully";
            } else {
                // Handle error response
                returnString = "Error creating file. Server responded with status code: " + statusCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            returnString = "An error occurred while creating file.";
        }
        return returnString;
    }

    public static String deleteFile(String email, String fileName) {
        try {
            // Create an HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create a Map to store email and fileName
            Map<String, String> requestData = new HashMap<>();
            requestData.put("userEmail", email);
            requestData.put("fileName", fileName);

            // Convert the Map to JSON using Gson
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(requestData);

            // Create a DELETE request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/deleteFile")) // Adjust the URL as needed
                    .method("DELETE", HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .header("Content-Type", "application/json")
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();

            switch (statusCode) {
                case 200:
                    // Delete successful
                    return "File deleted successfuly";
                case 404:
                    // File not found
                    return "File not found.";
                case 400:
                    return "Current user is not autheticated to delete the file";
                default:
                    // Other possible error responses
                    return "An error occurred while deleting the file. Server responded with status code: " + statusCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while deleting the file.";
        }
    }

    public static String renameFile(String email, String currentFileName, String newFileName) {
        try {
            // Convert parameters to JSON using Gson
            Gson gson = new Gson();
            Map<String, String> params = Map.of(
                    "userEmail", email,
                    "currentFileName", currentFileName,
                    "newFileName", newFileName);
            String json = gson.toJson(params);

            // Create an HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create a POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/renameFile")) // Adjust the URL as needed
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()) {
                case 200:
                    // Success response
                    return "";
                case 404:
                    return "Current file not found";
                case 403:
                    return "User does not have access to rename the file.";
                default:
                    return "An error occurred while renaming the file. Server responded with status code: " + response.statusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "An error occurred while renaming the file";
    }
    
    
    public static String moveFile(String email, String sourceName, String destinationPath) {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("userEmail", email);
        requestMap.put("sourceFileName", sourceName);
        requestMap.put("destinationPath", destinationPath);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(requestMap);

        // Assuming your move endpoint is located at this URL
        String url = "http://localhost:8000/moveFile";

        // Sending the HTTP request
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
             switch (httpResponse.statusCode()) {
                case 200:
                    // Parse the response if needed
                    return ""; // Return true if the copy was successful
                case 400:
                    return "User not autheticated to move file.";
                default:
                    return "Error in moving file. Server responded with status code : " + httpResponse.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }

        return "Error moving the file" ;
    }
    
    
        public static String copyFile(String email, String sourceName, String destinationPath) {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("userEmail", email);
        requestMap.put("sourceFileName", sourceName);
        requestMap.put("destinationPath", destinationPath);
          Gson gson = new Gson();
        String jsonRequest = gson.toJson(requestMap);

        // Assuming your copy endpoint is located at this URL
        String url = "http://localhost:8000/copyFile";

        // Sending the HTTP request
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            switch (httpResponse.statusCode()) {
                case 200:
                    // Parse the response if needed
                    return ""; // Return true if the copy was successful
                case 400:
                    return "User not autheticated to copy file.";
                default:
                    return "Error in copying file. Server responded with status code : " + httpResponse.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }

        return "Error copying the file" ; // Return false if the copy was unsuccessful
    }
}
