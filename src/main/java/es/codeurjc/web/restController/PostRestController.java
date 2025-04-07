package es.codeurjc.web.restController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.web.dto.CommentDTO;
import es.codeurjc.web.dto.PostDTO;
import es.codeurjc.web.dto.PostMapper;
import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.service.CommentService;
import es.codeurjc.web.service.PostService;

import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;



@RestController
@RequestMapping("/api/posts")
public class PostRestController {
    
    @Autowired
    private PostService postService;

    @Autowired
    private PostMapper postMapper;

    @Autowired CommentService commentService;
    @GetMapping("/")
    public Collection<PostDTO> getAllPosts() {
        return toDTOs(postService.findAll());
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
