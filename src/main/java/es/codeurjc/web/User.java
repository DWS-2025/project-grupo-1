package es.codeurjc.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class User {

    private String userName, password, description, userImage, email;
    private float rate;
    private List<Post> posts;
    private List<User> followers, following;
    private List<Section> followedSections;

    // Constructor with the information that the user provides when registering
    public User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.posts = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.rate = 0;
    }

    // Constructor for the user with all the information
    public User(String userName, String password, String description, String userImage, String email) {
        this.userName = userName;
        this.password = password;
        this.description = description;
        this.userImage = userImage;
        this.email = email;
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.followedSections = new ArrayList<>();
        this.posts = new ArrayList<>();
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
        post.addComment(new Comment(content, this, post, rate));
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

    public void setName(String userName) {
        this.userName = userName;
    }
    
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserImage() {
        return this.userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public float getRate() {
        return this.rate;
    }
 
    public List<Section> getFollowedSections() {
        return this.followedSections;
    }

    public void followSection(Section section) {
        this.followedSections.add(section);
    }

    @Override
    public boolean equals(Object obj) {
        return this.userName.equals(((User)obj).getName());
    }

}