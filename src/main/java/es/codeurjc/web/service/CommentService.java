package es.codeurjc.web.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.web.dto.CommentDTO;
import es.codeurjc.web.dto.CommentMapper;
import es.codeurjc.web.dto.CreateCommentDTO;
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

    public CommentDTO saveCommentInPost(Long postID, CreateCommentDTO commentDTO) {
        Comment comment = toDomain(commentDTO);
        Post postToComment = postService.findById(postID).get();

        //Ocurre lo mismo que en el tema de el discover y el following, no se por que al hacer la conversión de userDTO a user no funciona correctamente.
        //User currentUser = userMapper.toDomain(userService.getLoggedUser());
        User currentUser = userService.getLoggedUserDomain();
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
            

        userService.save(postToComment.getOwner());
        userService.save(currentUser);
        return toDTO(comment);
        
    }

    public Collection<CommentDTO> findAllCommentsByPostId(Long postId) {
        Post post = postService.findById(postId).get();
        return toDTOs(post.getComments());
    }

    public Page<CommentDTO> findAllCommentsByPostId(Long postId, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10); 
        Page<Comment> commentsPage = commentRepository.findByCommentedPost(postId, pageable); 
        return commentsPage.map(this::toDTO); 
    }
    public Page<CommentDTO> findAllComments(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10); 
        Page<Comment> commentsPage = commentRepository.findAll(pageable); 
        return commentsPage.map(this::toDTO); 
    }

    public void deleteCommentFromPost(Long commentedPostId, Long commentId) {
        Comment commentToDelete = commentRepository.findById(commentId).get();
        Post commentedPost = postService.findById(commentedPostId).get();
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

    public void updateComment(Long commentId, CommentDTO updatedCommentDTO, Long postId) {
        if (commentRepository.findById(commentId).isPresent() && postService.findById(postId).isPresent()) {
            Comment updatedComment = toDomain(updatedCommentDTO);
            Post commentedPost = postService.findById(postId).get();
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

    public Optional<Comment> findCommentById(Long id) {
        return commentRepository.findById(id);
    }
    public CommentDTO findCommentByIdDTO(Long id) {
        return toDTO(commentRepository.findById(id).orElseThrow());
    }

    // public CommentDTO findCommentById(Long id, Long postId) {
    //    return toDTO(commentRepository.findById(id).orElseThrow());
    // }

    private CommentDTO toDTO(Comment comment) {
        return mapper.toDTO(comment);
        
    }
    private Comment toDomain(CommentDTO commentDTO) {
        return mapper.toDomain(commentDTO);
    }
    private Comment toDomain(CreateCommentDTO commentDTO) {
        return mapper.toDomain(commentDTO);
    }
    public Collection<CommentDTO> toDTOs(Collection<Comment> comments) {
        return mapper.toDTOs(comments);
    }
  
}
