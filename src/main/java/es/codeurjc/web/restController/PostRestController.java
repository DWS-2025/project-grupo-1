package es.codeurjc.web.restController;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.dto.CommentDTO;
import es.codeurjc.web.dto.CreateCommentDTO;
import es.codeurjc.web.dto.CreatePostDTO;
import es.codeurjc.web.dto.PostDTO;
import es.codeurjc.web.service.CommentService;
import es.codeurjc.web.service.PostService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * REST controller for managing posts and their associated images and comments.
 * <p>
 * Provides endpoints for creating, retrieving, updating, and deleting posts,
 * as well as managing post images and comments. Supports pagination for listing
 * posts and comments. Enforces ownership checks for sensitive operations.
 * </p>
 *
 * <ul>
 *   <li>GET /api/posts/ - List posts (paginated)</li>
 *   <li>GET /api/posts/{id} - Retrieve a specific post</li>
 *   <li>POST /api/posts/ - Create a new post</li>
 *   <li>PUT /api/posts/{id} - Update an existing post (owner only)</li>
 *   <li>DELETE /api/posts/{id} - Delete a post (owner only)</li>
 *   <li>GET /api/posts/{id}/image - Retrieve post image</li>
 *   <li>POST /api/posts/{id}/image - Add image to post</li>
 *   <li>PUT /api/posts/{id}/image - Replace post image</li>
 *   <li>DELETE /api/posts/{id}/image - Delete post image</li>
 *   <li>GET /api/posts/comments - List all comments (paginated)</li>
 *   <li>GET /api/posts/{postId}/comments - List comments for a post (paginated)</li>
 *   <li>GET /api/posts/{postId}/comments/{commentId} - Retrieve a specific comment</li>
 *   <li>POST /api/posts/{postId}/comments - Add a comment to a post</li>
 *   <li>PUT /api/posts/{postId}/comments/{commentId} - Update a comment (owner only)</li>
 *   <li>DELETE /api/posts/{postId}/comments/{commentId}/ - Delete a comment (owner only)</li>
 * </ul>
 *
 * <p>
 * Requires authentication for certain operations and checks ownership for updates and deletions.
 * </p>
 *
 * @author Grupo 1
 */
@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/")
    public Page<CreatePostDTO> getPosts(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return postService.findAllAsCreateDTO(pageable);
    }

    @GetMapping("/{id}")
    public PostDTO getPost(@PathVariable Long id) {
        return postService.findByIdAsDTO(id);
    }

    @PostMapping("/")
    public ResponseEntity<PostDTO> createPost(@ModelAttribute CreatePostDTO createPostDTO,
            @RequestParam(value = "sections", required = false) List<Long> sectionIds,
            @RequestParam String newContributors, @RequestParam MultipartFile imageFile, HttpServletRequest request)
            throws IOException {

        if (createPostDTO.title().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        return ResponseEntity
                .ok(postService.save(createPostDTO, imageFile, sectionIds, newContributors.split(","), request));

    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @RequestParam String title,
            @RequestParam String content, @RequestParam MultipartFile newImageFile,
            @RequestParam(value = "sections", required = false) List<Long> newSectionIds,
            @RequestParam String newContributors, HttpServletRequest request) throws IOException {

        if (postService.checkIfUserIsTheOwner(id, request)) {
            PostDTO updated = postService.updatePost(id, title, content, newImageFile, newSectionIds,
                    newContributors.split(","), request);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(403).build(); // Forbidden
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, HttpServletRequest request) {
        if (postService.checkIfUserIsTheOwner(id, request)) {
            postService.deletePost(id); // Delete the post
            return ResponseEntity.noContent().build(); // No content to return
        } else {
            return ResponseEntity.status(403).build(); // Forbidden
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Object> getImageFile(@PathVariable Long id) throws SQLException, IOException {
        Resource postImage = postService.getImageFile(id);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(postImage);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Object> createPostImage(@PathVariable Long id, @RequestParam MultipartFile imageFile, HttpServletRequest request)
            throws IOException {

        if (postService.checkIfUserIsTheOwner(id, request)) {
            URI location = fromCurrentRequest().build().toUri();

            postService.createPostImage(id, location, imageFile.getInputStream(), imageFile.getSize());

            return ResponseEntity.created(location).build();
        
        } else {
            return ResponseEntity.status(403).build(); // Forbidden
        }
        

    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Object> replacePostImage(@PathVariable Long id, @RequestParam MultipartFile imageFile, HttpServletRequest request)
            throws IOException {

        if (postService.checkIfUserIsTheOwner(id, request)) {

            postService.replacePostImage(id, imageFile.getInputStream(), imageFile.getSize());

            return ResponseEntity.noContent().build();

        } else {
            return ResponseEntity.status(403).build(); // Forbidden
        }

    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Object> deletePostImage(@PathVariable Long id, HttpServletRequest request) throws IOException {

        if (postService.checkIfUserIsTheOwner(id, request)) {

            postService.deletePostImage(id);
            return ResponseEntity.noContent().build();
        
        } else {
            return ResponseEntity.status(403).build(); // Forbidden
            
        }

    }

    @GetMapping("/comments")
    // Get all comments for all posts
    public ResponseEntity<Page<CommentDTO>> getAllComments(@RequestParam(defaultValue = "0") int page) {
        Page<CommentDTO> commentsPage = commentService.findAllComments(page);
        return ResponseEntity.ok(commentsPage);
    }

    // Get all comments for a specific post
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Page<CommentDTO>> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page) {

        Page<CommentDTO> commentsPage = commentService.findAllCommentsByPostId(postId, page);
        return ResponseEntity.ok(commentsPage);
    }

    // Get a specific comment by ID
    @GetMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDTO> getCommentById(
            @PathVariable Long postId,
            @PathVariable Long commentId) {

        CommentDTO comment = commentService.findCommentByIdDTO(commentId);
        return ResponseEntity.ok(comment);
    }

    // New comment for a specific post
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long postId,
            @RequestBody CreateCommentDTO commentDTO, HttpServletRequest request) {

        CommentDTO savedComment = commentService.saveCommentInPost(postId, commentDTO, request);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedComment.id()).toUri();
        return ResponseEntity.created(location).body(savedComment);
        
    }

    // Update a specific comment in a post
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentDTO updatedCommentDTO, HttpServletRequest request) {
        if (commentService.checkIfCommentOwnerAndCommnetOnPost(request, postId, commentId)) {

            return ResponseEntity.ok(commentService.updateComment(commentId, updatedCommentDTO, postId));
        } else {
            return ResponseEntity.status(403).build(); // Forbidden
        }
    }

    // Delete a specific comment from a post
    @DeleteMapping("/{postId}/comments/{commentId}/")
    public ResponseEntity<Collection<CommentDTO>> deleteCommentFromPost(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            HttpServletRequest request) {
        if (commentService.checkIfCommentOwnerAndCommnetOnPost(request, postId, commentId)) {

            Collection<CommentDTO> comments = commentService.deleteCommentFromPostAPI(postId, commentId);
            return ResponseEntity.ok(comments);
        }
        return ResponseEntity.status(403).build(); // Forbidden
    }
}
