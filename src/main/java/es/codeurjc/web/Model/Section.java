package es.codeurjc.web.model;

import java.util.List;
import java.util.ArrayList;

public class Section {

     private long id;
     private String title;
     private String description;
     private String sectionImage;
     private List <Post> posts;
     private float averageRating;
     private int numberOfPublications;

     public Section(String title, String description, String sectionImage) {
          this.title = title;
          this.description = description;
          this.sectionImage = sectionImage;
          this.averageRating = 0;
          this.id = 0;
          this.numberOfPublications = 0;
          this.posts = new ArrayList<>();
     }

     public void addPost (Post post) {
          this.posts.add(post);
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
    
     public String getSectionImage() {
          return this.sectionImage;
     }

     public void setSectionImage(String sectionImage) {
          this.sectionImage = sectionImage;
     }
     
     public List<Post> getPosts() {
          return this.posts;
     }

     public float getAverageRating() {
          return this.averageRating;
     }

     public int getNumberOfPosts() {
          return this.posts.size();
     }  
    
}