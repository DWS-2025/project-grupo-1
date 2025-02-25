package es.codeurjc.web.Model;

import java.util.ArrayList;
import java.util.List;
public class Post {

    private long id;
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

    public Post(){
        
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void addContributor(User user) {
        this.contributors.add(user);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostImage() {
        return this.postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
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

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    public List<User> getContributors() {
        return this.contributors;
    }

    public Comment getComment(int index){
        return this.comments.get(index);
    }

    public void calculatePostAverageRating() { 
        List<Comment> comments = getComments();
               
        for (Comment comment: comments) {
            averageRating += comment.getRate();
        }
        averageRating /= comments.size();
        setAverageRating(averageRating); 
    }
   

   @Override
   public boolean equals(Object obj) {
       return this.title.equals(((Post)obj).getTitle()) && this.owner.equals(((Post)obj).getOwner());
   }
    
}
