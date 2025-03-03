package es.codeurjc.web.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.Comment;
import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.service.CommentService;
import es.codeurjc.web.service.ImagePostService;
import es.codeurjc.web.service.PostService;
import es.codeurjc.web.service.SectionService;

@Controller
public class PostController {

    private static final String POSTS_FOLDER = "posts";

    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ImagePostService imageService;
    @Autowired
    private SectionService sectionService;

    @GetMapping("/post")
    public String viewPosts(Model model) {
        model.addAttribute("posts", postService.findAllPosts());
        return "post_list";
    }

    @GetMapping("/post/new")
    public String createPost(Model model) {
        model.addAttribute("sections", sectionService.findAll());
        model.addAttribute("isEditing", false);
        return "post_form";
    }

    @PostMapping("/post/new")
    public String createPost(Model model, Post post, @RequestAttribute MultipartFile postImage, @RequestParam("sections") List<Long> sectionIds) throws IOException {  
        for (long sectionId : sectionIds) {
            post.addSection(sectionService.findById(sectionId).get());
        }
        postService.save(post);
        imageService.saveImage(POSTS_FOLDER, post.getId(), postImage);
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

    @GetMapping("/post/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws MalformedURLException {
        return imageService.createResponseFromImage(POSTS_FOLDER, id);
    }

    @GetMapping("/post/{id}/edit")
    public String editPost(Model model, @PathVariable long id) {
        Optional<Post> op = postService.findPostById(id);
        if (op.isPresent()) {
            Post post = op.get();

            List<Section> allSections = sectionService.findAll();
            List<Section> postSections = post.getSections();

            // Create a List<Section> with "selected" property
            List<Map<String, Object>> sectionsWithSelection = new ArrayList<>();
            for (Section section : allSections) {
                Map<String, Object> sectionData = new HashMap<>();
                sectionData.put("id", section.getId());
                sectionData.put("title", section.getTitle());
                
                // Verify if the section is in the post using its id
                boolean isSelected = postSections.stream()
                    .anyMatch(s -> s.getId() == section.getId());
                
                sectionData.put("selected", isSelected);
                sectionsWithSelection.add(sectionData);
            }

            model.addAttribute("sections", sectionsWithSelection);
            model.addAttribute("post", post);
            model.addAttribute("title", post.getTitle());
            model.addAttribute("content", post.getContent());
            model.addAttribute("isEditing", true);
            return "post_form";
        } else {
            return "post_not_found";
        }
    }

    @PostMapping("/post/{id}/edit")
    public String editPost(Model model, @PathVariable long id, Post updatedPost, @RequestAttribute MultipartFile postImage, @RequestParam("sections") List<Long> sectionIds) throws IOException {
        Optional<Post> op = postService.findPostById(id);
        if (op.isPresent()) {
            for (long sectionId : sectionIds) {
                updatedPost.addSection(sectionService.findById(sectionId).get());
            }

            postService.updatePost(op.get(), updatedPost, postImage);
            return "redirect:/post/" + id;
        } else {
            return "post_not_found";
        }
    }

    @GetMapping("/post/{id}/delete")
    public String deletePost(@PathVariable long id) {
        Optional<Post> op = postService.findPostById(id);
        if (op.isPresent()) {
            postService.deletePost(op.get());
            return "redirect:/post";
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
            model.addAttribute("message", "No se ha encontrado un post con ese nombre");
            return "error";
        }
    }

    @PostMapping("/post/{postId}/comment/new")
    public String newPostComment(Model model, @PathVariable long postId, Comment newComment) {
        Optional<Post> op = postService.findPostById(postId);
        if (op.isPresent()) {
            commentService.saveCommentInPost(op.get(), newComment);
            return "redirect:/post/" + postId;
        } else if (newComment.getContent().isEmpty()) {
            model.addAttribute("message", "El comentario no puede estar vacio");
            return "error";
        } else if (newComment.getRating() > 5 || newComment.getRating() < 0) {
            model.addAttribute("message", "El valor del rating debe estar entre 0 y 5");
            return "error";

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
            model.addAttribute("comment", opComment.get());
            model.addAttribute("isEditing", true);
            return "comment_form";
        } else {
            model.addAttribute("message", "No se ha encontrado el post o comentario especificado");
            return "post";
        }
    }

    @PostMapping("/post/{postId}/comment/{commentId}/edit")
    public String editPostCommentInfo(@PathVariable long postId, @PathVariable long commentId, Model model, Comment updatedComment) {
        Optional<Post> op = postService.findPostById(postId);
        Optional<Comment> opComment = commentService.findCommentById(commentId);
        if (op.isPresent() && opComment.isPresent()) {
            commentService.updateComment(commentId, updatedComment, op.get());
            return "redirect:/post/" + postId;
        } else if (updatedComment.getContent().isEmpty()) {
            model.addAttribute("message", "El comentario no puede estar vacio");
            return "error";
        } else if (updatedComment.getRating() > 5 || updatedComment.getRating() < 0) {
            model.addAttribute("message", "El valor del rating debe estar entre 0 y 5");
            return "error";

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
            model.addAttribute("message", "No se ha encontrado el comentario a borrar o el post especificado");
            return "error";
        }
    }

}
