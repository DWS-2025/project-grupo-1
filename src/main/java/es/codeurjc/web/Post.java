package es.codeurjc.web;

import java.util.ArrayList;
import java.util.List;
public class Post {
    private String title, content, postImage;
    private User owner;
    private String ownerName;
    private float  averageRating; // [0.00, 5.00]
    private List<Comment> comments;
    private List<User> contributors;

    public Post(String title, String content, String postImage, User owner){
        this.title = title;
        this.content = content;
        this.postImage = postImage;
        this.owner = owner;
        this.ownerName = owner.getName();
        this.averageRating = 0;
        this.comments = new ArrayList<Comment>();
        this.contributors = new ArrayList<User>();
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void addContributor(User user) {
        this.contributors.add(user);
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

    public User getOwner() {
        return this.owner;
    }

    public String getUsername() {
        return this.ownerName;
    }

    public float getAverageRating() {
        return this.averageRating;
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    public List<User> getContributors() {
        return this.contributors;
    }

    

   @Override
   public boolean equals(Object obj) {
       return this.title.equals(((Post)obj).getTitle()) && this.owner.equals(((Post)obj).getOwner());
   }
    
}
