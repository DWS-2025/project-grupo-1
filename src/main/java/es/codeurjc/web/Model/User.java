package es.codeurjc.web.Model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class User {

    private long id;
    private String userName, password, description, userImage, email;
    private float userRate, userPostRate;
    private List<Post> posts;
    private List<User> followers, followings;
    private List<Section> followedSections; 
    private List<Comment> comments;

    // Constructor with the information that the user provides when registering
    public User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.posts = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.followings = new ArrayList<>();
        this.followedSections = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.userRate = 0;
        this.userPostRate = 0;
    }

    // Delete the post from the user's posts if it exists
    public void deletePost(Post post) {
        posts.remove(post);
    }

    // Comment on a post 
    public void comment(Post post, String content) {
        post.addComment(new Comment(content, this, post, userPostRate));
    }

    // Follow a user
    public void follow(User user) {
        followings.add(user);
        user.followers.add(this);
    }

    // Unfollow a user
    public void unfollow(User user) {
        followings.remove(user);
        user.followers.remove(this);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
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
        return this.followings;
    }


    public void setUserRate(float userRate) {
        this.userRate = userRate;
    }

    public float getUserRate(){
        return this.userRate;
    }
    
 
    public List<Section> getFollowedSections() {
        return this.followedSections;
    }

    public void followSection(Section section) {
        this.followedSections.add(section);
    }


    public void calculateUserRate() {  
        List<Post> posts = getPosts();

        for(Post post : posts){
            userRate += post.getAverageRating();
        }
        setUserRate(userRate /= posts.size());
    }



    @Override
    public boolean equals(Object obj) {
        return this.userName.equals(((User)obj).getName());
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    public void addCommentToUser(Comment comment) {
        comments.add(comment);
    }
    

}