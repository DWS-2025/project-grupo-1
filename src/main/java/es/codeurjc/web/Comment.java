package es.codeurjc.web;

public class Comment {
    private String content;
    private User owner;
    private Post post;
    private int likes, dislikes;
    private float rate;

    public Comment(String content, User owner, Post post, float rate) {
        this.content = content;
        this.owner = owner;
        this.post = post;
        this.likes = 0;
        this.dislikes = 0;
        this.rate = rate;
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
        return this.rate;
    }
}
