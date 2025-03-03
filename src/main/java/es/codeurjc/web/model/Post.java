package es.codeurjc.web.model;

import java.util.ArrayList;
import java.util.List;
public class Post {

    private long id = 0;
    private String title, content;
    private User owner;
    private String ownerName;
    private float  averageRating = 0; // [0.00, 5.00]
    private List<Comment> comments = new ArrayList<>();
    private List<Section> sections = new ArrayList<>();
    private List<User> contributors = new ArrayList<>();

    public Post() {}

    public Post(String title, String content, String postImage){
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
        this.contributors.add(user);
        user.addCollaboratedPosts(this);
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

    public List<Section> getSections() {
        return this.sections;
    }

    public void calculatePostAverageRating() { 
        setAverageRating(0f);
        List<Comment> comments = getComments();

        for (Comment comment: comments) {
            averageRating += comment.getRating();
        }
        averageRating /= comments.size();
        setAverageRating(Math.round( averageRating * 10) / 10.0f); 
    }

    public void setOwnerName(String name){
        this.ownerName = name;
    }

    @Override
    public boolean equals(Object obj) {
        return this.title.equals(((Post)obj).getTitle()) && this.owner.equals(((Post)obj).getOwner());
    }

}
