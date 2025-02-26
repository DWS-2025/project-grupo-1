package es.codeurjc.web.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.web.Manager;
import es.codeurjc.web.Model.Comment;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.service.CommentService;
import es.codeurjc.web.service.PostService;
import es.codeurjc.web.service.UserService;

@Controller
public class PostController {
    @Autowired
    private Manager manager;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

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
        postService.savePost(post);
        return "view_post";
    }

    @GetMapping("/post/{id}")
    public String viewPost(Model model, @PathVariable long id) {
        Optional<Post> op = postService.findPostById(id);
        if (op.isPresent()) {
            model.addAttribute("post", op.get());
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

    @PostMapping("/post/{postId}/comment/new")
    public String newPostComment(@PathVariable long postId, Comment comment) {
        Optional<Post> op = postService.findPostById(postId);
        if (op.isPresent()) {
            commentService.saveCommentInPost(op.get(), comment);
            return "view_post";
        } else {
            return "post_not_found";
        }
    }

    @PostMapping("/post/{postId}/comment/{commentId}/edit")
    public String editPostComment(@PathVariable long postId, @PathVariable long commentId) {
        Optional<Post> op = postService.findPostById(postId);
        if (op.isPresent()) {
            commentService.deleteCommentFromPost(op.get(), commentId);
            return "view_post";
        } else {
            return "post_not_found";
        }
    }

    @PostMapping("/post/{postId}/comment/{commentId}/delete")
    public String deletePostComment(@PathVariable long postId, @PathVariable long commentId) {
        Optional<Post> op = postService.findPostById(postId);
        if (op.isPresent()) {
            commentService.deleteCommentFromPost(op.get(), commentId);
            return "view_post";
        } else {
            return "post_not_found";
        }
    }
    

}