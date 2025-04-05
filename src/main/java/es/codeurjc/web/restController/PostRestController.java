package es.codeurjc.web.restController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.web.dto.CommentDTO;
import es.codeurjc.web.dto.PostDTO;
import es.codeurjc.web.dto.PostMapper;
import es.codeurjc.web.service.CommentService;
import es.codeurjc.web.service.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


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
    //UNCOMPLETED -> it is just returning the comments of a post, not the post itself
 @GetMapping("/{id}")
    public ResponseEntity<Page<CommentDTO>> getCommentsByPostId(
            @PathVariable long postId,
            @RequestParam(defaultValue = "0") int page)
             {
    
        Page<CommentDTO> commentsPage = commentService.findAllCommentsByPostId(postId, page);
        return ResponseEntity.ok(commentsPage);
    }
}
