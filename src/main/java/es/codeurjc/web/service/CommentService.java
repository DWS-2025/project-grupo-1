package es.codeurjc.web.service;

import java.util.Collection;
import java.util.Optional;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.web.dto.CommentBasicDTO;
import es.codeurjc.web.dto.CommentDTO;
import es.codeurjc.web.dto.CommentMapper;
import es.codeurjc.web.dto.CreateCommentDTO;
import es.codeurjc.web.dto.UserBasicDTO;
import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.model.Comment;
import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.CommentRepository;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service class for managing comments in the application.
 * <p>
 * Provides methods for creating, retrieving, updating, and deleting comments associated with posts.
 * Handles comment sanitization, rating calculations for posts, users, and sections, and ensures
 * proper associations between comments, posts, and users.
 * </p>
 *
 * <p>
 * Main responsibilities:
 * <ul>
 *   <li>Saving new comments to posts with content sanitization.</li>
 *   <li>Retrieving comments by post or globally, with pagination support.</li>
 *   <li>Deleting comments from posts and updating related ratings.</li>
 *   <li>Updating existing comments and recalculating ratings.</li>
 *   <li>Checking comment ownership and association with posts for authorization.</li>
 *   <li>Mapping between domain and DTO representations of comments.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Dependencies:
 * <ul>
 *   <li>{@link CommentRepository} for persistence operations.</li>
 *   <li>{@link PostService} for post-related operations and rating calculations.</li>
 *   <li>{@link UserService} for user-related operations and authentication.</li>
 *   <li>{@link CommentMapper} for converting between domain and DTO objects.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Methods in this service may throw {@link IllegalArgumentException} if entities are not found.
 * </p>
 *
 * @author Grupo 1
 */
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

    public CommentDTO saveCommentInPost(Long postID, CreateCommentDTO commentDTO, HttpServletRequest request) {
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

        Comment comment = toDomain(commentDTO);
        Post postToComment = postService.findById(postID).get();
        String sanitizedContent = policy.sanitize(comment.getContent());
        comment.setContent(sanitizedContent);

        User currentUser = userService.getLoggedUserDomain(request.getUserPrincipal().getName());
        comment.setOwner(currentUser);
        comment.setCommentedPost(postToComment);

        commentRepository.save(comment);

        // Calculates the rating of the post
        postService.setAverageRatingPost(postToComment.getId());
        // Calculates the rating of the owner
        postToComment.getOwner().calculateUserRate();
        // Calculates the rating of the section
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

        postService.setAverageRatingPostRemoving(commentedPost.getId(), commentToDelete.getId());
        commentToDelete.setCommentedPost(null);

        // Calculates the rating of the post
        postService.saveForInit(commentedPost);

        // Calculates the rating of the owner
        commentedPost.getOwner().calculateUserRate();

        // Calculates the rating of the section

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

        // Calculates the rating of the owner
        commentedPost.getOwner().calculateUserRate();

        // Calculates the rating of the section
        for (Section section : commentedPost.getSections()) {
            section.calculateAverageRating();
        }
        return toDTOs(commentedPost.getComments());
    }

    public boolean checkIfCommentOwnerAndCommnetOnPost(HttpServletRequest request, Long postId, Long commentId) {
        UserDTO loggedUser = userService.findByUserNameDTO(request.getUserPrincipal().getName());
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
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

        if (commentRepository.findById(commentId).isPresent() && postService.findById(postId).isPresent()) {
            Comment commentToUpdate = commentRepository.findById(commentId).get();
            String sanitizedContent = policy.sanitize(updatedCommentDTO.content());
            Post commentedPost = postService.findById(postId).get();

            commentToUpdate.updateComment(sanitizedContent, updatedCommentDTO.rating());
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
