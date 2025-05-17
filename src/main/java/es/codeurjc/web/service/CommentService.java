package es.codeurjc.web.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.web.dto.CommentBasicDTO;
import es.codeurjc.web.dto.CommentDTO;
import es.codeurjc.web.dto.CommentMapper;
import es.codeurjc.web.dto.CreateCommentDTO;
import es.codeurjc.web.dto.PostMapper;
import es.codeurjc.web.dto.UserBasicDTO;
import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.dto.UserMapper;
import es.codeurjc.web.model.Comment;
import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.CommentRepository;
import jakarta.servlet.http.HttpServletRequest;

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

    public CommentDTO saveCommentInPost(Long postID, CreateCommentDTO commentDTO, HttpServletRequest request) {
        Comment comment = toDomain(commentDTO);
        Post postToComment = postService.findById(postID).get();

        User currentUser = userService.getLoggedUserDomain(request.getUserPrincipal().getName());
        comment.setOwner(currentUser);
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
        postService.saveForInit(commentedPost);
      
           
        // Calculates the rating of the post
        postService.setAverageRatingPostRemoving(commentedPost.getId(), commentToDelete.getId());
        commentToDelete.setCommentedPost(null);
        postService.saveForInit(commentedPost);
        //Calculates the rating of the owner
        commentedPost.getOwner().calculateUserRate();
        //Calculates the rating of the section

        for (Section section : commentedPost.getSections()) {
            section.calculateAverageRating();
        }
       
    }
    public Collection<CommentDTO> deleteCommentFromPostAPI(Long commentedPostId, Long commentId) {
        Comment commentToDelete = commentRepository.findById(commentId).get();
        Post commentedPost = postService.findById(commentedPostId).get();

      
           
        // Calculates the rating of the post
        postService.setAverageRatingPostRemoving(commentedPost.getId(), commentToDelete.getId());
        commentToDelete.setCommentedPost(null);
        commentedPost.getComments().remove(commentToDelete);
        postService.saveForInit(commentedPost);
        //Calculates the rating of the owner
        commentedPost.getOwner().calculateUserRate();
        //Calculates the rating of the section

        for (Section section : commentedPost.getSections()) {
            section.calculateAverageRating();
        }
       return toDTOs(commentedPost.getComments());
    }

    public boolean checkIfCommentOwnerAndCommnetOnPost(HttpServletRequest request, Long postId, Long commentId) {
        UserDTO loggedUser = userService.findByUserName(request.getUserPrincipal().getName());
        UserBasicDTO ownerUser = findCommentByIdDTO(commentId).owner();

        if (loggedUser.id().equals(ownerUser.id()) || loggedUser.userName().equals("Admin")) {

            if (postService.findByIdAsDTO(postId).comments().contains(findCommentByIdBasicDTO(commentId))) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }

    }

    public CommentDTO updateComment(Long commentId, CommentDTO updatedCommentDTO, Long postId) {
        if (commentRepository.findById(commentId).isPresent() && postService.findById(postId).isPresent()) {
            Comment commentToUpdate = commentRepository.findById(commentId).get();
            Post commentedPost = postService.findById(postId).get();
            commentToUpdate.updateComment(updatedCommentDTO.content(), updatedCommentDTO.rating());
            commentRepository.save(commentToUpdate);

            postService.setAverageRatingPost(commentedPost.getId());
            commentedPost.getOwner().calculateUserRate();
            for (Section section : commentedPost.getSections()) {
                section.calculateAverageRating();
            }
            userService.save(commentedPost.getOwner());
            return toDTO(commentToUpdate);
        } else {
            throw new IllegalArgumentException("Comment or Post not found");
        }
    }

    public Optional<Comment> findCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public CommentBasicDTO findCommentByIdBasicDTO(Long id) {
        return toCommentBasicDTO(commentRepository.findById(id).orElseThrow());
    }

    public CommentDTO findCommentByIdDTO(Long id) {
        return toDTO(commentRepository.findById(id).orElseThrow());
    }

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

    public CommentBasicDTO toCommentBasicDTO(Comment comment) {
        return mapper.toCommentBasicDTO(comment);
    }

}
