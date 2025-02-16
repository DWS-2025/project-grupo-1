package es.codeurjc.web;

public class Comment {
    private String content;
    private User owner;
    private Post post;
    private int likes;

    public Comment(String content, User owner, Post post) {
        this.content = content;
        this.owner = owner;
        this.post = post;
        this.likes = 0;
    }

    public void like() {
        this.likes++;
    }

    public void dislike() {
        this.likes--;
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
    
}
