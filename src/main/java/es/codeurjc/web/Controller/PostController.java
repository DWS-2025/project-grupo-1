package es.codeurjc.web.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.web.Model.Comment;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.service.CommentService;
import es.codeurjc.web.service.PostService;
import jakarta.servlet.ServletRequest;

@Controller
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/post")
    public String viewPosts(Model model) {
        model.addAttribute("posts", postService.findAllPosts());
        return "posts";
    }

    @GetMapping("/post/new")
    public String getMethodName(Model model) {
        return "post-form";
    }

    @PostMapping("/post/new")
    public String newPost(Model model, Post post) {
        postService.save(post);
        return "view_post";
    }

    @GetMapping("/post/{id}")
    public String viewPost(Model model, @PathVariable long id) {
        Optional<Post> op = postService.findPostById(id);
        if (op.isPresent()) {
            
            model.addAttribute("post", op.get());
            model.addAttribute("Comments", op.get().getComments());
            
            return "view_post";
        } else {
            return "post_not_found";
        }
    }

    @GetMapping("/post/{id}/edit")
    public String editPost(Model model, @PathVariable long id) {
        Optional<Post> op = postService.findPostById(id);
        if (op.isPresent()) {
            model.addAttribute("post", op.get());
            return "post-form";
        } else {
            return "post_not_found";
        }
    }

    @PostMapping("/post/{id}/delete")
    public String deletePost(@PathVariable long id) {
        Optional<Post> op = postService.findPostById(id);
        if (op.isPresent()) {
            postService.deletePost(op.get());
            return "posts";
        } else {
            return "post_not_found";
        }
    }

    @GetMapping("/post/{postId}/comment/new")
    public String newPostCommentForm(Model model, @PathVariable long postId) {
        Optional<Post> op = postService.findPostById(postId);
        if (op.isPresent()) {
            model.addAttribute("post", op.get());
      
            return "comment_form";
        } else {
            model.addAttribute("errorType", "No se ha encontrado un post con ese nombre");
            return "error";
        }
    }

    @PostMapping("/post/{postId}/comment/new")
    public String newPostComment(Model model, @PathVariable long postId, Comment newComment, ServletRequest request) {
        Optional<Post> op = postService.findPostById(postId);
        if (op.isPresent()) {
            commentService.saveCommentInPost(op.get(), newComment);
            return "redirect:/post/" + postId; 
        } else {
            model.addAttribute("errorType", "No se ha encontrado un post con ese nombre");
            return "error";
        }
    }
    // this should work, but the user can delete a comment from a post that is not on that post (manipulating the request?), need to be implemented a checker
    @GetMapping("/post/{postId}/comment/{commentId}/edit")
    public String editPostComment(@PathVariable long postId, @PathVariable long commentId, Model model) {
        Optional<Post> op = postService.findPostById(postId);
        Optional<Comment> opComment = commentService.findCommentById(commentId);
        if (op.isPresent() && opComment.isPresent()) {
            model.addAttribute("post", op.get());
            model.addAttribute("Comment", opComment.get());
            model.addAttribute("isEditing", true);
            return "comment_form";
        } else {
            model.addAttribute("message", "No se ha encontrado un post o comentario con ese nombre");
            return "post";
        }    
    }
    @PostMapping("/post/{postId}/comment/{commentId}/edit")
    public String editPostCommentInfo(@PathVariable long postId, @PathVariable long commentId, Model model, Comment updatedComment) {
        Optional<Post> op = postService.findPostById(postId);
        Optional<Comment> opComment = commentService.findCommentById(commentId);
        if (op.isPresent() && opComment.isPresent()) {
            commentService.updateComment(commentId, updatedComment);
            return "redirect:/post/" + postId;
        } else {
            return "post_not_found";
        }    
    }

    @PostMapping("/post/{postId}/comment/{commentId}/delete")
    public String deletePostComment(@PathVariable long postId, @PathVariable long commentId, Model model) {
        Optional<Post> op = postService.findPostById(postId);
        Optional<Comment> opComment = commentService.findCommentById(commentId);
        if (op.isPresent() && opComment.isPresent()) {
            commentService.deleteCommentFromPost(op.get(), commentId);
            model.addAttribute("post", op.get());
            model.addAttribute("Comments", op.get().getComments());         
            return "redirect:/post/" + postId;
        } else {
            return "post_not_found";
        }
    }

}
