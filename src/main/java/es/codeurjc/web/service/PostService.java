package es.codeurjc.web.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Comment;
import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

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

    public void updatePost(Post post, Post updatedPost) {
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setPostImage(updatedPost.getPostImage());
        postRepository.save(post);
    }
    public CommentService getCommentService() {
        return this.commentService;
    }
   

}
