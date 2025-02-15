package es.codeurjc.web;

public class Section {
     private String title;
     private String description;
     private String sectionImage;
     private Post posts[];
  
    
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
    
     public Post[] getPosts() {
          return this.posts;
     }
    
    
}
