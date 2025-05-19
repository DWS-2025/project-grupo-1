package es.codeurjc.web.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;

/**
 * Represents a section in the application, which can contain multiple posts.
 * Each section has a title, description, image (as a URL or Blob), an average rating,
 * and a count of the number of publications (posts) it contains.
 *
 * <p>
 * The Section entity is mapped to a database table using JPA annotations.
 * It maintains a many-to-many relationship with the {@link Post} entity.
 * </p>
 *
 * <p>
 * Key features:
 * <ul>
 *   <li>Stores both a URL and a Blob for the section image.</li>
 *   <li>Keeps track of the average rating of its posts.</li>
 *   <li>Provides methods to add and remove posts, and to calculate the average rating.</li>
 *   <li>Overrides {@code equals} and {@code hashCode} based on the section's ID.</li>
 * </ul>
 * </p>
 * 
 * @author Grupo 1
 */
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private String description;
    private String image; // URL of the image
    private float averageRating;
    private int numberOfPublications;

    @Lob
    private Blob imageFile; // Blob for storing the image file

    @ManyToMany
    private List<Post> posts = new ArrayList<>();

    public Section() {
    }

    public Section(String title, String description) {
        this.title = title;
        this.description = description;
        this.averageRating = 0;
        this.id = 0;
        this.numberOfPublications = 0;
    }

    public void addPost(Post post) {
        posts.add(post);
        numberOfPublications++;
    }

    public void addPostToSection(Post post) {
        post.addSection(this);

    }

    public void deletePost(Post post) {
        this.numberOfPublications--;
        this.posts.remove(post);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Blob getImageFile() {
        return this.imageFile;
    }

    public void setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public List<Post> getPosts() {
        return this.posts;
    }

    public float getAverageRating() {
        return this.averageRating;
    }

    public int getNumberOfPosts() {
        return this.numberOfPublications;
    }

    public void setAverageRating(float rate) {
        this.averageRating = rate;

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void calculateAverageRating() {
        List<Post> posts = getPosts();
        if (posts.isEmpty()) {
            setAverageRating(0);
            return;
        }

        float totalRating = 0;
        int ratedPostsCount = 0;

        for (Post post : posts) {
            if (post.getAverageRating() > 0) { // Check if the post has a rating
                totalRating += post.getAverageRating();
                ratedPostsCount++;
            }
        }

        if (ratedPostsCount > 0) {
            setAverageRating(totalRating / ratedPostsCount);
        } else {
            setAverageRating(0);
        }
    }

    // Overriding equals and hashCode methods to compare sections by their id
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
