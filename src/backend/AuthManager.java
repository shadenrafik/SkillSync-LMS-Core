package backend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import model.*;

public class AuthManager {
    private JsonDatabaseManager dbManager;
    public AuthManager(){
        dbManager=new JsonDatabaseManager();
    }

    public boolean validateEmail(String email){
        if (email==null||email.isEmpty()) return false;
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}(\\.[a-z]{2,})?$");
    }

    public boolean validateRequiredFields(String username,String email,String password,String role){
        return !(username==null||username.isEmpty()||email==null||email.isEmpty()|| password==null||password.isEmpty()||role==null||role.isEmpty());
    }

    public String hashPasswordSHA256(String password){
        try{
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            byte[] hashBytes=digest.digest(password.getBytes());
            StringBuilder sb=new StringBuilder();
            for (byte b :hashBytes){
                sb.append(String.format("%02x",b));
            }
            return sb.toString();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean signup(String username,String email,String password,String role){
        List<User>users=dbManager.loadUsers();
        username=username.trim();
        password=password.trim();
        role=role.toLowerCase();
        email=email.trim().toLowerCase();
        if (!validateRequiredFields(username,email,password,role)){
            System.out.println("All fields are required!");
            return false;
        }
        if (!validateEmail(email)){
            System.out.println("Invalid email format!");
            return false;
        }
        if (dbManager.isEmailDuplicate(users, email)){
            System.out.println("Email already exists!");
            return false;
        }
        String userId=dbManager.generateUserId(users);
        String hashedPassword=hashPasswordSHA256(password);
        User newUser;
        if(role.equalsIgnoreCase("admin")){                 //added for admin role
            newUser=new Admin(userId,username,email,hashedPassword);    
        }else if (role.equalsIgnoreCase("student")){
            newUser=new Student(userId,username,email,hashedPassword);
        }else if (role.equalsIgnoreCase("instructor")){
            newUser=new Instructor(userId,username,email,hashedPassword);
        }else{
            System.out.println("Invalid role!");
            return false;
        }
        users.add(newUser);
        dbManager.saveUsers(users);
        System.out.println("Signup successful for "+role+": "+username);
        return true;
    }

    public String login(String email,String password){
        System.out.println("Attempting login for email: "+email);               //for testing
        email=email.trim().toLowerCase();
        password=password.trim();
        List<User>users=dbManager.loadUsers();
        String hashedInput=hashPasswordSHA256(password);
        for (User u :users){
            System.out.println("Checking user: "+u.getEmail());                 //for testing
            if (u.getEmail().equalsIgnoreCase(email)){
                if (u.getPasswordHash().equals(hashedInput)){
                    System.out.println("Login successful! Role: "+u.getRole());
                    return u.getRole();
                }else{
                    System.out.println("Incorrect password!");
                    return null;
                }
            }
        }
        System.out.println("Email not found!");
        return null;
    }

    public void logout(){
        System.out.println("User logged out!");
    }

}