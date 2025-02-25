package es.codeurjc.web.service;

import java.util.List;
import java.util.Optional;

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
        postToComment.getComments().add(comment);
        User owner = userService.getUserById(owner.getId()); // cambiar, hay que comprobar que el user esta loggeado
        owner.getComments().add(comment);
        commentRepository.saveInRepository(comment);
    }

    public void deleteCommentInPost (Post commentedPost, Long commentId){
        Comment commentToDelete = commentRepository.findBy(commentId).get();    
        commentedPost.getComments().remove(commentToDelete);
        User owner = commentToDelete.getOwner();
        owner.getComments().remove(commentToDelete);
        commentRepository.deleteComment(commentToDelete);
    }



}
