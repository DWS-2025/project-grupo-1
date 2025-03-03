package es.codeurjc.web.model;

public class Comment {

    private long id;
    private String content;
    private User owner;
    private Post commentedPost;
    private int likes, dislikes;
    private String commentOwnerName;
    private int rating;

    //Constructor for the comments made by the user
    public Comment(String content, int rating) {
        this.content = content;
        this.likes = 0;
        this.dislikes = 0;
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

    public int getLikes() {
        return this.likes;
    }

    public int getDislikes() {
        return this.dislikes;
    }

    public String getOwnerName() {
        return this.commentOwnerName;
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
        if (!content.equals(this.content)) {
            this.setContent(content);
        }
        if (rating != this.rating) {
            this.setRating(rating);
        }
    }

   

}
