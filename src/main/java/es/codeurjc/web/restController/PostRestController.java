package es.codeurjc.web.restController;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.web.dto.CommentDTO;
import es.codeurjc.web.dto.CreateCommentDTO;
import es.codeurjc.web.dto.CreatePostDTO;
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
    public Page<CreatePostDTO> getPosts(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return postService.findAllAsCreateDTO(pageable);
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
    public ResponseEntity<PostDTO> createPost(@RequestBody CreatePostDTO createPostDTO, MultipartFile imageFile)
            throws IOException {
        PostDTO postDTO = postService.save(createPostDTO, imageFile); // Save the post and image
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

    @GetMapping("/{id}/image")
    public ResponseEntity<Object> getImageFile(@PathVariable long id) throws SQLException, IOException {
        Resource postImage = postService.getImageFile(id);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(postImage);
    }

    @PostMapping("/{id}/image")
	public ResponseEntity<Object> createPostImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {

		URI location = fromCurrentRequest().build().toUri();

		postService.createPostImage(id, location, imageFile.getInputStream(), imageFile.getSize());

		return ResponseEntity.created(location).build();

	}

    @PutMapping("/{id}/image")
	public ResponseEntity<Object> replacePostImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {

		postService.replacePostImage(id, imageFile.getInputStream(), imageFile.getSize());

		return ResponseEntity.noContent().build();
	}

    @DeleteMapping("/{id}/image")
	public ResponseEntity<Object> deletePostImage(@PathVariable long id) throws IOException {

		postService.deletePostImage(id);

		return ResponseEntity.noContent().build();
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
            @RequestBody CreateCommentDTO commentDTO) {

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
    @DeleteMapping("/{postId}/comments/{commentId}")
    public void deleteCommentFromPost(@RequestParam long postId, @RequestParam long commentId) {
        commentService.deleteCommentFromPost(commentId, postId);

    }
}
