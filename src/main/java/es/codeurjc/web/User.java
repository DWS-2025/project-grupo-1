package es.codeurjc.web;

public class User {

    private String name, email, password, description, userImage;

    public User(String name, String email, String password, String description, String userImage) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.description = description;
        this.userImage = userImage;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
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
