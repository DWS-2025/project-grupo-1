package es.codeurjc.web;

public class Comment {
    private String content;
    private User owner;
    private Post post;
    private int likes, dislikes;
    private float postRate;

    //Constructor for the initial comments
    public Comment(String content, User owner, Post post, float rate) {
        this.content = content;
        this.owner = owner;
        this.post = post;
        this.likes = 0;
        this.dislikes = 0;
        this.postRate = rate;
    }
    //Constructor for the comments made by the user
    public Comment(String content, User owner, float rate) {
        this.content = content;
        this.owner = owner;
        this.likes = 0;
        this.dislikes = 0;
        this.postRate = rate;
    }

    public void like() {
        this.likes++;
    }

    public void addDislike() {
        this.dislikes++;
    }
    
    public String getContent() {
        return this.content;
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
