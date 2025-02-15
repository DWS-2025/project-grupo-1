package es.codeurjc.web;

public class User {

    private String userName, password, description, userImage;
    private Post posts[];
    private User followers[];
    private User following[];

    // Constructor with the information that the user provides when registering
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getName() {
        return this.userName;
    }
    
    public String getPassword() {
        return this.password;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUserImage() {
        return this.userImage;
    }

}
