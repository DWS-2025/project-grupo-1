package es.codeurjc.web;

import java.util.List;

public class Section {
     private String title;
     private String description;
     private String sectionImage;
     private List <Post> posts; // he cambiado el array por la lista, mas facil de manejar
  
    
     public Section(String title, String description, String sectionImage) {
          this.title = title;
          this.description = description;
          this.sectionImage = sectionImage;
         
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
    
     
    
}
