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

    @PostMapping("/post/{postId}/comment/new/")
    public String newPostComment(Model model, @PathVariable long postId, Comment updatedComment) {
        Optional<Post> op = postService.findPostById(postId);
        if (op.isPresent()) {
            commentService.saveCommentInPost(op.get(), updatedComment);
            return "view_post" + postId;
        } else {
            model.addAttribute("errorType", "No se ha encontrado un post con ese nombre");
            return "error";
        }
    }
    // this should work, but the user can delete a comment from a post that is not on that post, need to be implemented a checker
    @GetMapping("/post/{postId}/comment/{commentId}/edit")
    public String editPostComment(@PathVariable long postId, @PathVariable long commentId, Model model) {
        Optional<Post> op = postService.findPostById(postId);
        Optional<Comment> opComment = commentService.findCommentById(commentId);
        if (op.isPresent() && opComment.isPresent()) {
            model.addAttribute("Post", op.get());
            model.addAttribute("Comment", opComment.get());
            model.addAttribute("isEditing", true);
            return "comment_form";
        } else {
            return "post_not_found";
        }    
    }
    @PostMapping("/post/{postId}/comment/{commentId}/edit")
    public String editPostCommentInfo(@PathVariable long postId, @PathVariable long commentId, Model model, Comment updatedComment) {
        Optional<Post> op = postService.findPostById(postId);
        Optional<Comment> opComment = commentService.findCommentById(commentId);
        if (op.isPresent() && opComment.isPresent()) {
            commentService.updateComment(commentId, updatedComment);
            return "view_post" + postId;
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


    /*@GetMapping("/comment_form/{postTitle}")
    public String commentForm(Model model, @PathVariable String postTitle) {
        Post requestedPost = new Post();
    
        // We check if the post exists, if it doesn't we show an error page explaining
        // the problem.
        if (requestedPost.getTitle() == null) {
            model.addAttribute("errorType", "No se ha encontrado ningun post con el titulo :" + postTitle);
            return "error";
        } else {
            model.addAttribute("isEditing", false);
            model.addAttribute("Post", requestedPost);
            return "/comment_form";
        }

    }

    @PostMapping("/comment_form/{postTitle}")
    public String sendComment(Model model, @RequestParam String content, @RequestParam int rating,
            @PathVariable String postTitle) {
        Post requestedPost = new Post();
        for (Post post : manager.getAplicationPosts()) {
            if (post.getTitle().equals(postTitle)) {
                requestedPost = post;
            }
        }
        // We check if the post exists, if it doesn't we show an error page explaining
        // the problem.
        if (requestedPost.getTitle() == null) {
            model.addAttribute("errorType", "No se ha encontrado ningun post con el titulo :" + postTitle);
            return "error";
        } else if (rating < 0 || rating > 5 ) {
            model.addAttribute("errorType", "La valoración debe estar entre 0 y 5");
            return "error";
        } else if (content == null || content.isEmpty()) {
            model.addAttribute("errorType", "El comentario no puede estar vacio");
            return "error";
        } else {
            Comment newComment = new Comment(content, manager.getMainUser(), rating);
            requestedPost.addComment(newComment);
            return "redirect:/view_post/" + postTitle;
        }

    }

    @GetMapping("/edit_comment/{postTitle}/{commentId}")
    public String editCommentForm(Model model, @PathVariable String postTitle, @PathVariable int commentId) {
        Post requestedPost = new Post();
        Comment requestedComment = new Comment();

        // Look for the post and the comment
        for (Post post : manager.getAplicationPosts()) {
            if (post.getTitle().equals(postTitle)) {
                requestedPost = post;
                // We get the comment with the id commentId, -1 because the id starts at 1 {{-index}})
                break;
            }
        }

        if (requestedPost.getTitle() == null) {
            model.addAttribute("errorType", "No se ha encontrado ningún post con el título: " + postTitle);
            return "error";
        } 
           
        model.addAttribute("Post", requestedPost);
        model.addAttribute("isEditing", true);
        model.addAttribute("commentId", commentId);
        model.addAttribute("content", requestedPost.getComment(commentId-1).getCommentContent());
        return "comment_form";
    }

    @PostMapping("/edit_comment/{postTitle}/{commentId}")
    public String updateComment(Model model, @PathVariable String postTitle, @PathVariable int commentId, @RequestParam String content, @RequestParam int rating) {
        Post requestedPost = new Post();
        for (Post post : manager.getAplicationPosts()) {
            if (post.getTitle().equals(postTitle)) {
                requestedPost = post;     
                break;
            }
        }
        if (rating < 0 || rating > 5) {
            model.addAttribute("errorType", "La valoración debe estar entre 0 y 5");
            return "error";
        } else if (content == null || content.isEmpty()) {
            model.addAttribute("errorType", "El comentario no puede estar vacio");
            return "error";
        } else {
            requestedPost.getComment(commentId - 1).updateComment(content, rating);
            model.addAttribute("Post", requestedPost);
            return "redirect:/view_post" + postTitle;
        }
    }
     */
}
