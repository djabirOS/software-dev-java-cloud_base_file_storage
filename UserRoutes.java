/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafrontend;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserRoutes {

    public static String loginUser(String email, String password) {
        try {
            // Create an HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create a JSON object containing the email and password
            JsonObject loginDetails = new JsonObject();
            loginDetails.addProperty("email", email);
            loginDetails.addProperty("password", password);

            // Convert JSON object to String
            String jsonRequest = loginDetails.toString();

            // Create a POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/login")) // Adjust the URL as needed
                    .method("POST", HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .header("Content-Type", "application/json")
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            switch (statusCode) {
                case 200:
                    // Login successful
                    return "";
                case 401:
                    return "Email or password incorrect.";
                default:
                    return "An error occurred while logging in. Server responded with status code: " + statusCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while logging in.";
        }
    }

    public static String registerUser(String name, String email, String password, String contact, String address) {
        try {
            // Create an HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create a JSON object containing the user's registration details
            JsonObject registrationDetails = new JsonObject();
            registrationDetails.addProperty("name", name);
            registrationDetails.addProperty("email", email);
            registrationDetails.addProperty("password", password);
            registrationDetails.addProperty("contact", contact);
            registrationDetails.addProperty("address", address);

            // Convert JSON object to String using Gson
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(registrationDetails);

            // Create a POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/addUser")) // Adjust the URL as needed
                    .method("POST", HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .header("Content-Type", "application/json")
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            switch (statusCode) {
                case 200:
                    // Registration successful
                    return "";
                case 400:
                    return "Invalid registration details.";
                default:
                    return "An error occurred while registering. Server responded with status code: " + statusCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while registering.";
        }
    }

}
