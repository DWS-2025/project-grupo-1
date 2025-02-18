package es.codeurjc.web;

public class Comment {
    private String content;
    private User owner;
    private Post post;
    private int likes;
    private int dislikes;

    public Comment(String content, User owner, Post post) {
        this.content = content;
        this.owner = owner;
        this.post = post;
        this.likes = 0;
    }

    public void like() {
        this.likes++;
    }

    public void dislike() { // yo quitaria esta, me parece que tiene mas sentido tener a parte los dislikes en lugar de un contador de solo likes
        this.likes--;
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
}
