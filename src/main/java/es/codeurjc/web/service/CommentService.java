package es.codeurjc.web.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Comment;
import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.CommentRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    UserService userService;

    public void saveCommentInPost(Post postToComment, Comment comment) {
        User currentUser = userService.getLoggedUser();
        comment.setOwner(currentUser);
        comment.setCommentOwnerName(currentUser.getName());
        postToComment.getComments().add(comment);

        postToComment.calculatePostAverageRating();
        postToComment.getOwner().calculateUserRate();
        currentUser.getComments().add(comment);

        commentRepository.save(comment);
    }

    public void deleteCommentFromPost(Post commentedPost, Long commentId) {
        Comment commentToDelete = commentRepository.findById(commentId).get();
        commentedPost.getComments().remove(commentToDelete);
        User owner = userService.getLoggedUser();
        owner.getComments().remove(commentToDelete);

        commentedPost.calculatePostAverageRating();
        commentedPost.getOwner().calculateUserRate();
        commentRepository.deleteComment(commentToDelete);
    }

    public void updateComment(Long commentId, Comment updatedComment, Post commentedPost) {
        if (commentRepository.findById(commentId).isPresent()) {
            commentRepository.findById(commentId).get().updateComment(updatedComment.getContent(), updatedComment.getRating());

            commentedPost.calculatePostAverageRating();
            commentedPost.getOwner().calculateUserRate();
        } else {
            // not found
        }
    }

    public Optional<Comment> findCommentById(long id) {
        return commentRepository.findById(id);
    }
}
