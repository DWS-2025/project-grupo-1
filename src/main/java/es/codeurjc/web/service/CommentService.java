package es.codeurjc.web.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.dto.CommentDTO;
import es.codeurjc.web.dto.CommentMapper;
import es.codeurjc.web.dto.PostDTO;
import es.codeurjc.web.dto.PostMapper;
import es.codeurjc.web.dto.UserMapper;
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

    @Autowired
    private CommentMapper mapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserMapper userMapper;

    public CommentDTO saveCommentInPost(PostDTO postDTO, CommentDTO commentDTO) {
        Comment comment = toDomain(commentDTO);
        Post postToComment = postMapper.toDomain(postDTO);

    
        User currentUser = userMapper.toDomain(userService.getLoggedUser());
        comment.setOwner(currentUser);
        comment.setCommentOwnerName(currentUser.getUserName());
        comment.setCommentedPost(postToComment);
        
        commentRepository.save(comment);

        // Calculates the rating of the post 
        postService.setAverageRatingPost(postToComment.getId());
        //Calculates the rating of the owner
        postToComment.getOwner().calculateUserRate();
        //Calculates the rating of the section
        for (Section section : postToComment.getSections()) {
            section.calculateAverageRating();
        }
            

        // currentUser.getComments().add(comment); creo que no haria falta ya que al comentario le estamos asignando directamente un usuario (owner) -> preguntar en clase
        userService.save(postToComment.getOwner());
        userService.save(currentUser);
        return toDTO(comment);
        
       
    }

    public void deleteCommentFromPost(Post commentedPost, Long commentId) {
        Comment commentToDelete = commentRepository.findById(commentId).get();
        commentedPost.getComments().remove(commentToDelete);

        commentRepository.delete(commentToDelete);
        // Calculates the rating of the post
        postService.setAverageRatingPost(commentedPost.getId());
        //Calculates the rating of the owner
        commentedPost.getOwner().calculateUserRate();
        //Calculates the rating of the section

        for (Section section : commentedPost.getSections()) {
            section.calculateAverageRating();
        }
        commentRepository.delete(commentToDelete);
    }

    public void updateComment(Long commentId, Comment updatedComment, Post commentedPost) {
        if (commentRepository.findById(commentId).isPresent()) {
            commentRepository.findById(commentId).get().updateComment(updatedComment.getContent(), updatedComment.getRating());
            commentRepository.save(commentRepository.findById(commentId).get());

            postService.setAverageRatingPost(commentedPost.getId());
            commentedPost.getOwner().calculateUserRate();
            for (Section section : commentedPost.getSections()) {
                section.calculateAverageRating();
            }
            userService.save(commentedPost.getOwner());
            

        } else {
            // not found
        }
    }

    public Optional<Comment> findCommentById(long id) {
        return commentRepository.findById(id);
    }

    private CommentDTO toDTO(Comment comment) {
        return mapper.toDTO(comment);
        
    }
    private Comment toDomain(CommentDTO commentDTO) {
        return mapper.toDomain(commentDTO);
    }
    public Collection<CommentDTO> toDTOs(Collection<Comment> comments) {
        return mapper.toDTOs(comments);
    }
  
}
