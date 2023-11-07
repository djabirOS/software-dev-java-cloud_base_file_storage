/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafrontend;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.util.Arrays;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class Routes {

    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();

        try {
            // Create an HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create a GET request to your server's endpoint
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/getUsers")) // Adjust the URL as needed
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            if (statusCode == 200) {
                // Convert the JSON response to User objects
                String json = response.body();
                User[] userArray = new Gson().fromJson(json, User[].class); // You need to have the Gson library
                users = Arrays.asList(userArray);
            } else {
                System.out.println("Error fetching users. Server responded with status code: " + statusCode);
                System.out.println("Response body: " + response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("users : " + users);
        return users;
    }

    public static String editUser(User user) {
        String returnString = "";
        try {
            // Convert User object to JSON using Gson
            Gson gson = new Gson();
            String json = gson.toJson(user);

            // Create an HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create a PUT request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/editUser")) // Adjust the URL as needed
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            switch (statusCode) {
                case 200:
                    // Success response
                    System.out.println("User updated successfully");
                    returnString = "User updated successfully";
                    break;
                case 404:
                    // Error response for user not found
                    System.out.println("Error updating user. User not found.");
                    System.out.println("Response body: " + response.body());
                    returnString = "Error updating user. User not found.";
                    break;
                default:
                    // Handle other possible error responses
                    System.out.println("Error updating user. Server responded with status code: " + statusCode);
                    System.out.println("Response body: " + response.body());
                    returnString = "Error updating user. Server responded with status code: " + statusCode;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while updating user.");
            return "An error occurred while updating user.";
        }
        return returnString;
    }

    public static String deleteUser(String email) {
        String returnString = "";
        try {
            // Create a Map object containing the email
            Map<String, String> emailMap = new HashMap<>();
            emailMap.put("email", email);

            // Convert the Map to JSON using Gson
            Gson gson = new Gson();
            String json = gson.toJson(emailMap);

            // Create an HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create a DELETE request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/deleteUser")) // Adjust the URL as needed
                    .method("DELETE", HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            switch (statusCode) {
                case 200:
                    // Success response
                    System.out.println("User deleted successfully");
                    returnString = "User deleted successfully";
                    break;
                case 404:
                    // Error response for email not found
                    System.out.println("Error deleting user. Email not found.");
                    System.out.println("Response body: " + response.body());
                    returnString = "Error deleting user. Email not found.";
                    break;
                default:
                    // Handle other possible error responses
                    System.out.println("Error deleting user. Server responded with status code: " + statusCode);
                    System.out.println("Response body: " + response.body());
                    returnString = "Error deleting user. Server responded with status code: " + statusCode;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while deleting user.");
            return "An error occurred while deleting user.";
        }
        return returnString;
    }

    public static String addUser(User user) {

        String returnString = "";
        try {

            // Convert User object to JSON using Gson
            Gson gson = new Gson();
            String json = gson.toJson(user);

            // Create an HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create a POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/addUser")) // Adjust the URL as needed
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            switch (statusCode) {
                case 200:
                    // Success response
                    System.out.println("User registered successfully");
                    returnString = "User registered successfully";
                    break;
                case 400:
                    // Error response for email already registered
                    System.out.println("Error registering user. Email is already registered.");
                    System.out.println("Response body: " + response.body());
                    returnString = "Error registering user. Email is already registered.";
                    break;
                default:
                    // Handle other possible error responses
                    System.out.println("Error registering user. Server responded with status code: " + statusCode);
                    System.out.println("Response body: " + response.body());
                    returnString = "Error registering user. Server responded with status code: " + statusCode;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while registering user.");
            return "An error occurred while registering user.";
        }
        return returnString;
    }

}
