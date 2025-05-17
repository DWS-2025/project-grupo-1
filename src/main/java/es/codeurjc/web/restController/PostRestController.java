package es.codeurjc.web.restController;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
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
    public ResponseEntity<PostDTO> createPost(@RequestBody CreatePostDTO createPostDTO, MultipartFile imageFile, HttpServletRequest request)
            throws IOException {
        PostDTO postDTO = postService.save(createPostDTO, imageFile, request); // Save the post and image
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(postDTO.id()).toUri(); // URI for the new post
        return ResponseEntity.created(location).body(postDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @RequestBody CreatePostDTO newCreatePostDTO, @RequestAttribute MultipartFile newImageFile, @RequestParam(value = "sections", required = false) List<Long> newSectionIds, @RequestParam("newContributors") String newContributorsStrings, HttpServletRequest request) throws IOException {

        if (postService.checkIfUserIsTheOwner(id, request)) {
            PostDTO updated = postService.updatePost(id, newCreatePostDTO, newSectionIds, newContributorsStrings.split(","), newImageFile);
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
    public ResponseEntity<Object> createPostImage(@PathVariable Long id, @RequestParam MultipartFile imageFile) throws IOException {

        URI location = fromCurrentRequest().build().toUri();

        postService.createPostImage(id, location, imageFile.getInputStream(), imageFile.getSize());

        return ResponseEntity.created(location).build();

    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Object> replacePostImage(@PathVariable Long id, @RequestParam MultipartFile imageFile) throws IOException {

        postService.replacePostImage(id, imageFile.getInputStream(), imageFile.getSize());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Object> deletePostImage(@PathVariable Long id) throws IOException {

        postService.deletePostImage(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/edit")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @GetMapping("/comments")
    //Get all comments for all posts
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
        // CommentDTO comment = commentService.findCommentById(commentId, postId);
        return ResponseEntity.ok(comment);
    }

    // new comment for a specific post
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long postId,
            @RequestBody CreateCommentDTO commentDTO, HttpServletRequest request) {

        CommentDTO savedComment = commentService.saveCommentInPost(postId, commentDTO, request);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedComment.id()).toUri();
        return ResponseEntity.created(location).body(savedComment);
    }

    // update a specific comment in a post
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentDTO updatedCommentDTO, HttpServletRequest request) {
        if (commentService.checkIfCommentOwnerAndCommnetOnPost(request, postId, commentId)) {
            commentService.updateComment(commentId, updatedCommentDTO, postId);
            return ResponseEntity.ok(updatedCommentDTO);
        } else {
            return ResponseEntity.status(403).build(); // Forbidden
        }
    }

    // delete a specific comment from a post
    @DeleteMapping("/{postId}/comments/{commentId}")
    public void deleteCommentFromPost(@RequestParam Long postId, @RequestParam Long commentId, HttpServletRequest request) {
        if (commentService.checkIfCommentOwnerAndCommnetOnPost(request, postId, commentId)) {
            commentService.deleteCommentFromPost(commentId, postId);
        }
    }
    
}
