package es.codeurjc.web.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * Represents a blog post entity in the application.
 * <p>
 * Each post has a title, content, optional image, and can be associated with an owner (author),
 * contributors, comments, and sections. The post also maintains an average rating.
 * </p>
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the post.</li>
 *   <li>{@code title} - Title of the post (max 100 characters).</li>
 *   <li>{@code content} - Main content of the post (max 2000 characters).</li>
 *   <li>{@code image} - Path or URL to an image associated with the post.</li>
 *   <li>{@code imageFile} - Binary large object (BLOB) for storing image data.</li>
 *   <li>{@code owner} - The user who created the post.</li>
 *   <li>{@code averageRating} - Average rating of the post (0 to 5).</li>
 *   <li>{@code comments} - List of comments associated with the post.</li>
 *   <li>{@code sections} - List of sections this post belongs to.</li>
 *   <li>{@code contributors} - List of users who contributed to the post.</li>
 * </ul>
 *
 * <p>
 * Provides methods to manage comments, sections, contributors, and post attributes.
 * </p>
 * 
 * @author Grupo 1
 */
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 100)
    private String title;
    @Column(length = 2000)
    private String content;

    private String image;
    @Lob
    private Blob imageFile;

    @ManyToOne
    private User owner;
    private float averageRating = 0; // (0, 1, 2, 3, 4, 5)

    @OneToMany(mappedBy = "commentedPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(mappedBy = "posts")
    private List<Section> sections = new ArrayList<>();

    @ManyToMany
    private List<User> contributors = new ArrayList<>();

    public Post() {
    }

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public Comment getComment(int index) {
        return this.comments.get(index);
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void deleteSection(Section section) {
        this.sections.remove(section);
    }

    public List<Long> getSectionsIds() {
        List<Long> sectionsIds = new ArrayList<>();
        for (Section section : sections) {
            sectionsIds.add(section.getId());
        }
        return sectionsIds;
    }

    public void addContributor(User user) {
        if (!contributors.contains(user)) {
            this.contributors.add(user);
            user.addCollaboratedPosts(this);
        }

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

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Blob getImageFile() {
        return this.imageFile;
    }

    public void setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getOwner() {
        return this.owner;
    }

    public float getAverageRating() {
        return this.averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    public List<User> getContributors() {
        return this.contributors;
    }

    public void setContributors(List<User> contributors) {
        this.contributors = contributors;
    }

    public List<Section> getSections() {
        return this.sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);

    }

    @Override
    public boolean equals(Object obj) {
        return this.title.equals(((Post) obj).getTitle()) && this.owner.equals(((Post) obj).getOwner());
    }

}
