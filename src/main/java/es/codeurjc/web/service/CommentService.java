package es.codeurjc.web.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Comment;
import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.CommentRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;	

    @Autowired
    UserService userService;

    public void saveCommentInPost(Post postToComment, Comment comment) {
        User currentUser = userService.getLoggedUser();
        comment.setOwner(currentUser);
        comment.setCommentOwnerName(currentUser.getName());
        comment.setCommentedPost(postToComment);
        // postToComment.getComments().add(comment); creo que no haria falta ya que al comentario le estamos asignando directamente un post -> preguntar en clase

        // Calculates the rating of the post
        postToComment.calculatePostAverageRating();
        //Calculates the rating of the owner
        postToComment.getOwner().calculateUserRate();
        //Calculates the rating of the section
        for (Section section : postToComment.getSections()) {
            section.calculateAverageRating();
        }

        // currentUser.getComments().add(comment); creo que no haria falta ya que al comentario le estamos asignando directamente un usuario (owner) -> preguntar en clase
        userService.save(currentUser);
        postService.save(postToComment);
        commentRepository.save(comment);
    }

    public void deleteCommentFromPost(Post commentedPost, Long commentId) {
        Comment commentToDelete = commentRepository.findById(commentId).get();
        // Por que si  no estoy añadiendo al post explicitamente los comentarios, hay que borrarlos asi?
        commentedPost.getComments().remove(commentToDelete);
        
        // Calculates the rating of the post
        commentedPost.calculatePostAverageRating();
        //Calculates the rating of the owner
        commentedPost.getOwner().calculateUserRate();
        //Calculates the rating of the section

        for (Section section : commentedPost.getSections()) {
            section.calculateAverageRating();
        }
        postService.save(commentedPost);
        commentRepository.delete(commentToDelete);
    }

    public void updateComment(Long commentId, Comment updatedComment, Post commentedPost) {
        if (commentRepository.findById(commentId).isPresent()) {
            commentRepository.findById(commentId).get().updateComment(updatedComment.getContent(), updatedComment.getRating());

            commentedPost.calculatePostAverageRating();
            commentedPost.getOwner().calculateUserRate();
            for (Section section : commentedPost.getSections()) {
                section.calculateAverageRating();
            }
            commentRepository.save(commentRepository.findById(commentId).get());
            postService.save(commentedPost);
        } else {
            // not found
        }
    }

    public Optional<Comment> findCommentById(long id) {
        return commentRepository.findById(id);
    }
}
