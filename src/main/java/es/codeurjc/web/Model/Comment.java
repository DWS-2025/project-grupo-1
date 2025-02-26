package es.codeurjc.web.Model;

public class Comment {
    
    private long id;
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

    public void dislike() {
        this.dislikes++;
    }
    
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCommentContent() {
        return this.commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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
    public void setRate(float rate) {
        this.postRate = rate;
    }

    public void updateComment(String content, float rate) {
        if(!content.equals(this.commentContent))
        this.setCommentContent(content);
        if(rate != this.postRate)
        this.setRate(rate);
    }
    
    // Rate a post
    public void ratePost(Post post, float rating) {
        
    }

}
