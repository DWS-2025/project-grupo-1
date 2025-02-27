package es.codeurjc.web.service;


import org.springframework.beans.factory.annotation.Autowired;

import es.codeurjc.web.Model.Comment;
import es.codeurjc.web.Model.Post; 
import es.codeurjc.web.Model.User;
import es.codeurjc.web.Repository.CommentRepository;

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
        Comment commentToDelete = commentRepository.findBy(commentId).get();    
        commentedPost.getComments().remove(commentToDelete);
        User owner = userService.getUserById(0);
        owner.getComments().remove(commentToDelete);
        commentRepository.deleteComment(commentToDelete);
    }
    public void updateComment (Long commentId, String newContent, int newRating){
        if (commentRepository.findBy(commentId).isPresent()) {
             commentRepository.findBy(commentId).get().updateComment(newContent, newRating);
        } else {
            // not found
        }       
    }



}
