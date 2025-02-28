package es.codeurjc.web.model;

public class Comment {

    private long id;
    private String commentContent;
    private User owner;
    private Post post;
    private int likes, dislikes;
    private String commentOwnerName;
    private int rating;

    //Constructor for the comments made by the user
    public Comment(String commentContent, int rating) {
        this.commentContent = commentContent;
        this.likes = 0;
        this.dislikes = 0;
        this.rating = rating;
    }

    //Constructor for the initial comments
    public Comment() {

    }

    public Comment(String commentContent, User owner, Post post, int rating) {
        this.commentContent = commentContent;
        this.owner = owner;
        this.post = post;
        this.likes = 0;
        this.dislikes = 0;
        this.rating = rating;
    }

    public void like() {
        this.likes++;
    }

    public void dislike() {
        this.dislikes++;
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

    public int getRating() {
        return this.rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void updateComment(String content, int rating) {
        if (!content.equals(this.commentContent)) {
            this.setCommentContent(content);
        }
        if (rating != this.rating) {
            this.setRating(rating);
        }
    }

   

}
