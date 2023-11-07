/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javabackend;

import java.io.Serializable;

public class UserPermissions implements Serializable{
    public String fileName;
    
    public boolean edit;
    public boolean read;
    public boolean delete;
    public boolean move;
    public boolean copy;
   
    
}
