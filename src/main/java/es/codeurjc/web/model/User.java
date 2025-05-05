package es.codeurjc.web.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity(name = "UserTable")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String userName, password, description, email;
    private float userRate;
    private String image;

    @Lob
    private Blob userImage;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
    @ManyToMany(fetch=FetchType.EAGER)
    private List<User> followers;
    @ManyToMany(fetch=FetchType.EAGER)
    private List<User> followings;
    @ManyToMany(fetch=FetchType.EAGER)
    private List<Section> followedSections;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    @ManyToMany(mappedBy = "contributors")
    private List<Post> collaboratedPosts;

    // Constructor with the information that the user provides when registering
    public User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.posts = new ArrayList<>();
        this.collaboratedPosts = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.followings = new ArrayList<>();
        this.followedSections = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.userRate = 0;
    }

    public User() {
    }

    // Delete the post from the user's posts if it exists
    public void deletePost(Post post) {
        posts.remove(post);
    }

    // Follow a user
    public void follow(User user) {
        if (!this.followings.contains(user) && !user.followers.contains(this)) {
        this.followings.add(user);
        user.followers.add(this);
    }
    }

    // Unfollow a user
    public void unfollow(User user) {
        this.followings.remove(user);
        user.followers.remove(this);
    }

    public void unfollowSection(Section section) {
        this.followedSections.remove(section);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
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

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }  

    public Blob getUserImage() {
        return this.userImage;
    }

    public void setUserImage(Blob userImage) {
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

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<User> getFollowers() {
        return this.followers;
    }

    public List<User> getFollowings() {
        return this.followings;
    }

    public void setUserRate(float userRate) {
        this.userRate = userRate;
    }

    public float getUserRate() {
        return this.userRate;
    }

    public List<Section> getFollowedSections() {
        return this.followedSections;
    }

    public void followSection(Section section) {
        this.followedSections.add(section);
        
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    public void addCommentToUser(Comment comment) {
        comments.add(comment);
    }

    public List<Post> getCollaboratedPosts() {
        return this.collaboratedPosts;

    }

    public void addCollaboratedPosts(Post collaboratedPost) {
        this.collaboratedPosts.add(collaboratedPost);
    }

    public void calculateUserRate() {
        List<Post> posts = getPosts();
        // Index for counting the posts with comments
        int index = 0;
        setUserRate(0);
        if (posts.isEmpty()) {
            return;
        }

        for (Post post : posts) {
            if (!post.getComments().isEmpty()) {
              userRate += post.getAverageRating();
                index++;
            }
        }
        if (index != 0) {
            setUserRate(userRate /= index);
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        return this.userName.equals(other.getUserName());
    }

}
