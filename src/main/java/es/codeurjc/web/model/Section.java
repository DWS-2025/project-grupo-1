package es.codeurjc.web.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;

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
    public void addPostToSection(Post post){
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

   /* public String getimageFile() {
        return this.imageFile;
    }

    public void setimageFile(String imageFile) {
        this.imageFile = imageFile;
    }*/ 

    public Blob getImageFile(){
        return this.imageFile;
    }

    public void setImageFile(Blob imageFile){
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
    public void setAverageRating(float rate){
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
        //Index for counting the posts with comments
        int index = 0;
        setAverageRating(0);
        for (Post post : posts) {
            if (!post.getComments().isEmpty()) {
                averageRating += post.getAverageRating();
                index++;
            }
        }
        if (index != 0) {
            setAverageRating(averageRating / index);
        }
     }

}
