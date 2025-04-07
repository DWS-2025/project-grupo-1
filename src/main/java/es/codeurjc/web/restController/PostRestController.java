package es.codeurjc.web.restController;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.web.dto.CommentDTO;
import es.codeurjc.web.dto.PostBasicDTO;
import es.codeurjc.web.dto.PostDTO;
import es.codeurjc.web.dto.PostMapper;
import es.codeurjc.web.service.CommentService;
import es.codeurjc.web.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentService commentService;

    @GetMapping("/")
    public Page<PostDTO> getPosts(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return postService.findAllAsDTO(pageable);
    }

    @GetMapping("/{id}")
    public PostDTO getPost(@PathVariable long id) {
        Optional<PostDTO> op = postService.findByIdDTO(id);
        if (op.isPresent()) {
            return op.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }

    @PostMapping("/")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO, MultipartFile imageFile)
            throws IOException {
        postDTO = postService.save(postDTO, imageFile); // Save the post and image
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(postDTO.id()).toUri(); // URI for the new post
        return ResponseEntity.created(location).body(postDTO);
    }

    @PutMapping("/{id}")
    public PostDTO updatePost(@PathVariable long id, @RequestBody PostDTO oldPostDTO, @RequestAttribute MultipartFile newImageFile, @RequestParam(value = "sections", required = false) List<Long> newSectionIds, @RequestParam("newContributors") String newContributorsStrings) throws IOException {
        PostDTO newPostDTO = postService.findByIdDTO(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        return postService.updatePost(oldPostDTO, newPostDTO, newSectionIds, newContributorsStrings.split(","), newImageFile); // Update the post and image
    }

    @DeleteMapping("/{id}")
    public PostDTO deletePost(@PathVariable long id) {
        PostDTO postToDelete = postService.findByIdDTO(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        postService.deletePost(postToDelete); // Delete the post
        return postToDelete;
    }

    // Get all comments for a specific post
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Page<CommentDTO>> getCommentsByPostId(
            @PathVariable long postId,
            @RequestParam(defaultValue = "0") int page) {

        Page<CommentDTO> commentsPage = commentService.findAllCommentsByPostId(postId, page);
        return ResponseEntity.ok(commentsPage);
    }

    // Get a specific comment by ID
    @GetMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDTO> getCommentById(
            @PathVariable long postId,
            @PathVariable long commentId) {

        CommentDTO comment = commentService.findCommentById(commentId, postId);
        return ResponseEntity.ok(comment);
    }

    // new comment for a specific post
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable long postId,
            @RequestBody CommentDTO commentDTO) {

        CommentDTO savedComment = commentService.saveCommentInPost(postId, commentDTO);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedComment.id()).toUri();
        return ResponseEntity.created(location).body(savedComment);
    }

    // update a specific comment in a post
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable long postId,
            @PathVariable long commentId,
            @RequestBody CommentDTO updatedCommentDTO) {

        commentService.updateComment(commentId, updatedCommentDTO, postId);
        return ResponseEntity.ok(updatedCommentDTO);
    }

    // delete a specific comment from a post
    public void deleteCommentFromPost(long postId, long commentId) {
        commentService.deleteCommentFromPost(commentId, postId);

    }
}
