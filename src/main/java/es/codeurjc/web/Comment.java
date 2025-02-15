package es.codeurjc.web;

public class Comment {
    private String content;
    private User propietaryUser;
    private Post post;
    private int likes;

    public Comment(String content, User propietaryUser, Post post) {
        this.content = content;
        this.propietaryUser = propietaryUser;
        this.post = post;
    }

    public String getContent() {
        return this.content;
    }

    public User getPropietaryUser() {
        return this.propietaryUser;
    }

    public Post getPost() {
        return this.post;
    }

    
}
