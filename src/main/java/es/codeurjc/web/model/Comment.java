package es.codeurjc.web.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(length = 2000) 
    private String content;

    @ManyToOne
    private User owner;
    // esto hay que quitarlo cuando tengamos BD 
    @ManyToOne
    private Post commentedPost;
    private String commentOwnerName;
    private int rating;

    //Constructor for the comments made by the user
    public Comment(String content, int rating) {
        this.content = content;
        this.rating = rating;
    }

    //Constructor for the initial comments
    public Comment() {

    }

    public Comment(String content, User owner, Post post, int rating) {
        this.content = content;
        this.owner = owner;
        this.commentOwnerName = owner.getName();
        this.commentedPost = post;
        this.rating = rating;
    }

    public void setCommentOwnerName(String commentOwnerName) {
        this.commentOwnerName = commentOwnerName;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Post getCommentedPost() {
        return this.commentedPost;
    }

    public String getCommentOwnerName() {
        return this.commentOwnerName;
    }


    public int getRating() {
        return this.rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    public void setCommentedPost(Post commentedPost) {
        this.commentedPost = commentedPost;
    }

    public void updateComment(String content, int rating) {
        if (!content.equals(this.content)) {
            this.setContent(content);
        }
        if (rating != this.rating) {
            this.setRating(rating);
        }
    }

   

}
