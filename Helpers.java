/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javabackend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Permissions;
import java.util.HashMap;


public class Helpers {

    public static void savePermissionsToFile(HashMap<String, UserPermissions> permissionsMap) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("permissions.ser"))) {
            oos.writeObject(permissionsMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, UserPermissions> loadPermissionsFromFile() {
        File file = new File("permissions.ser");
        HashMap<String, UserPermissions> permissionsMap = null;
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                permissionsMap = (HashMap<String, UserPermissions>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return permissionsMap;
    }
    

}
