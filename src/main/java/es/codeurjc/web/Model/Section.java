package es.codeurjc.web.Model;

import java.util.List;

public class Section {
     private String title;
     private String description;
     private String sectionImage;
     private List <Post> posts;
     private  float averageRating;
     private int numberOfPublications = 0;
    
  
    
     public Section(String title, String description, String sectionImage, float averageRating) {
    
          this.title = title;
          this.description = description;
          this.sectionImage = sectionImage;
          this.averageRating = averageRating;
         
     }
     public float getAverageRating() {
          return this.averageRating;
     }
    
     public String getTitle() {
          return this.title;
     }
    
     public String getDescription() {
          return this.description;
     }
    
     public String getSectionImage() {
          return this.sectionImage;
     }
    
     public void addPost (Post post) {
          this.posts.add(post);
     }
     
     public List<Post> getPosts() {
          return this.posts;
     }
     public int getNumberOfPosts() {
          return this.posts.size();
     }
     public void deletePost(Post post) {
          this.numberOfPublications--;
          this.posts.remove(post);
     }
  
     
    
}