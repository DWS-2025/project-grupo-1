package es.codeurjc.web;

public class Comment {
    private String commentContent;
    private User owner;
    private String commentOwnerName;
    private Post post;
    private int likes, dislikes;
    
    private float postRate;

    //Constructor for the initial comments
    public Comment(){
        
    }
    public Comment(String commentContent, User owner, Post post, float rate) {
        this.commentContent = commentContent;
        this.owner = owner;
        this.post = post;
        this.likes = 0;
        this.commentOwnerName = owner.getName();
        this.dislikes = 0;
        this.postRate = rate;
    }
    //Constructor for the comments made by the user
    public Comment(String commentContent, User owner, float rate) {
        this.commentContent = commentContent;
        this.owner = owner;
        this.likes = 0;
        this.dislikes = 0;
        this.postRate = rate;
        this.commentOwnerName = owner.getName();
    }

    public void like() {
        this.likes++;
    }

    public void addDislike() {
        this.dislikes++;
    }
    
    public String getcommentContent() {
        return this.commentContent;
    }

    public User getOwner() {
        return this.owner;
    }

    public Post getPost() {
        return this.post;
    }

    public int getLikes() {
        return this.likes;
    }
    
    public int getDislikes() {
        return this.dislikes;
    }

    public int getTotalLikes() {
        return this.likes - this.dislikes;
    }

    public float getRate() {
        return this.postRate;
    }

    
    // Rate a post
    public void ratePost(Post post, float rating) {
        
    }



}
