package es.codeurjc.web.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.Model.Comment;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.Model.User;
import es.codeurjc.web.Repository.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImagePostService imageService;

    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> findPostById(long id) {
        return postRepository.findById(id);
    }

    public void save(Post post) {
        User currentUser = userService.getLoggedUser();
        post.setOwner(currentUser);
        currentUser.getPosts().add(post);
        postRepository.save(post);
    }
    public void saveOtherUsersPost(Post post, User user) {
        post.setOwner(user);
        post.setOwnerName(user.getName());
        user.getPosts().add(post);
        postRepository.save(post);
    }
    
    public void deletePost(Post post) {
        for (Comment comment : post.getComments()) {
            commentService.deleteCommentFromPost(post, comment.getId());
        }
        postRepository.deleteById(post.getId());
        post.getComments().clear();
    }

    public void updatePost(Post post, Post updatedPost, MultipartFile postImage) throws IOException {
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        imageService.deleteImage("posts", post.getId());
        imageService.saveImage("posts", post.getId(), postImage);
        // post.setPostImage(updatedPost.getPostImage());
        postRepository.save(post);
    }
    public CommentService getCommentService() {
        return this.commentService;
    }
   

}
