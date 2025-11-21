package model;

public class Admin extends User {


    public Admin(String userId, String username, String email, String passwordHash) {
        super(userId, username, email, passwordHash, "admin");
    }



    @Override
    public String toString() {
        return "Admin{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
    
}
