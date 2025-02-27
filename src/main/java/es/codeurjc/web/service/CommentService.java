package es.codeurjc.web.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.Model.Comment;
import es.codeurjc.web.Model.Post; 
import es.codeurjc.web.Model.User;
import es.codeurjc.web.Repository.CommentRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    UserService userService;


    public void saveCommentInPost (Post postToComment, Comment comment){
        User currentUser = userService.getUserById(0);
        comment.setOwner(currentUser);
        postToComment.getComments().add(comment);
		currentUser.getComments().add(comment);
		commentRepository.saveInRepository(comment); 
    }

    public void deleteCommentFromPost (Post commentedPost, Long commentId){
        Comment commentToDelete = commentRepository.findById(commentId).get();    
        commentedPost.getComments().remove(commentToDelete);
        User owner = userService.getUserById(0);
        owner.getComments().remove(commentToDelete);
        commentRepository.deleteComment(commentToDelete);
    }
    public void updateComment (Long commentId, Comment updatedComment){
        if (commentRepository.findById(commentId).isPresent()) {
             commentRepository.findById(commentId).get().updateComment(updatedComment.getCommentContent(), updatedComment.getRate());
        } else {
            // not found
        }    
  
    }
     public Optional<Comment> findCommentById(long id) {
        return commentRepository.findById(id);
    }

 
    



}
