package es.codeurjc.web;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String userName, password, description, userImage, email;
    private float rate;
    private List<Post> posts;
    private List<User> followers, following;

    // Constructor with the information that the user provides when registering
    public User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.posts = new ArrayList<Post>();
        this.followers = new ArrayList<User>();
        this.following = new ArrayList<User>();
        this.rate = 0;
    }

    // Create a new post
    public Post createPost(String title, String content, String postImage) {
        return new Post(title, content, postImage, this);
    }

    // Add the post to the user's posts
    public void addPost(Post post) {
        posts.add(post);
    }

    // Delete the post from the user's posts if it exists
    public void deletePost(Post post) {
        posts.remove(post);
    }

    // Comment on a post 
    public void comment(Post post, String content) {
        post.addComment(new Comment(content, this, post));
    }

    // Follow a user
    public void follow(User user) {
        following.add(user);
        user.followers.add(this);
    }

    // Unfollow a user
    public void unfollow(User user) {
        following.remove(user);
        user.followers.remove(this);
    }

    // Rate a post
    public void ratePost(Post post, float rating) {
        // TODO: Implement rating system
    }

    public String getName() {
        return this.userName;
    }
    
    public String getPassword() {
        return this.password;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUserImage() {
        return this.userImage;
    }

    public List<Post> getPosts() {
        return this.posts;
    }

    public List<User> getFollowers() {
        return this.followers;
    }

    public List<User> getFollowing() {
        return this.following;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public float getRate() {
        return this.rate;
    }

    @Override
    public boolean equals(Object obj) {
        return this.userName.equals(((User)obj).getName());
    }

}
