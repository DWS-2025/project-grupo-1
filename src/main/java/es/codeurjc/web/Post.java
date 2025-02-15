package es.codeurjc.web;

public class Post {
    private String title, content, postImage;
    private User propietaryUser; 
    private float  averageRating;
    private Comment comments[];
    private User colaboUsers[];

    public Post(String title, String content, String postImage, User propietaryUser){
        this.title = title;
        this.content = content;
        this.postImage = postImage;
        this.propietaryUser = propietaryUser;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public String getPostImage() {
        return this.postImage;
    }

    public User getPropietaryUser() {
        return this.propietaryUser;
    }

    public float getAverageRating() {
        return this.averageRating;
    }

    public Comment[] getComments() {
        return this.comments;
    }

    public User[] getColaboUsers() {
        return this.colaboUsers;
    }

   
    

    
}
