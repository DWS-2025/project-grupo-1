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

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(length = 100) 
    private String title;
    @Column(length = 2000) 
    private String content;
    
    @Lob
    private Blob postImage;

    @ManyToOne
    private User owner;
    private String ownerName;
    private float  averageRating = 0; // [0.00, 5.00]
    
    @OneToMany(mappedBy = "commentedPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    
    @ManyToMany(mappedBy = "posts")
    private List<Section> sections = new ArrayList<>();
    
    @ManyToMany
    private List<User> contributors = new ArrayList<>();

    public Post() {}

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
        if(!contributors.contains(user)) {
            this.contributors.add(user);
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

    public Blob getPostImage() {
        return this.postImage;
    }

    public void setPostImage(Blob postImage) {
        this.postImage = postImage;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getOwner() {
        return this.owner;
    }

    public String getUsername() {
        return this.ownerName;
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

    public void setOwnerName(String name){
        this.ownerName = name;
    }

    @Override
    public boolean equals(Object obj) {
        return this.title.equals(((Post)obj).getTitle()) && this.owner.equals(((Post)obj).getOwner());
    }

}
