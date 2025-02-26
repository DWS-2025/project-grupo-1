package es.codeurjc.web.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import es.codeurjc.web.Model.Comment;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.Repository.PostRepository;

public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentService commentService;

    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> findPostById(long id) {
        return postRepository.findById(id);
    }

    public void savePost(Post post) {
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
