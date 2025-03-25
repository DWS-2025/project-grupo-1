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
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.CommentService;
import es.codeurjc.web.service.ImagePostService;
import es.codeurjc.web.service.PostService;
import es.codeurjc.web.service.SectionService;
import es.codeurjc.web.service.UserService;

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
    @Autowired
    private UserService userService;

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
    public String createPost(Model model, Post post, @RequestAttribute MultipartFile postImage, String contributors, @RequestParam(value = "sections", required = false) List<Long> sectionIds) throws IOException {  
        if (sectionIds != null) {    
            for (long sectionId : sectionIds) {
                post.addSection(sectionService.findById(sectionId).get());
            }
        }
        
        String[] contributorsArray = contributors.split(",");
        for (String colaborator : contributorsArray) {
            User user = userService.findByUserName(colaborator);
            if (user != null) {
                post.addContributor(user);
            }
        }

        postService.save(post, postImage);
        return "redirect:/post";
    }

    @GetMapping("/post/{id}")
    public String viewPost(Model model, @PathVariable long id) {
        Optional<Post> op = postService.findPostById(id);
        if (op.isPresent()) {
            model.addAttribute("post", op.get());
            return "view_post";

        } else {
            model.addAttribute("message", "No se ha encontrado un post con ese nombre");
            return "error";
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

            String contributors = "";
            for (User user : post.getContributors()) {
                contributors += user.getName() + ",";
            }

            model.addAttribute("sections", sectionsWithSelection);
            model.addAttribute("post", post);
            model.addAttribute("title", post.getTitle());
            model.addAttribute("content", post.getContent());
            model.addAttribute("contributors", contributors);
            model.addAttribute("isEditing", true);
            return "post_form";

        } else {
            model.addAttribute("message", "No se ha encontrado un post con ese nombre");
            return "error";
        }
    }

    @PostMapping("/post/{id}/edit")
    public String editPost(Model model, @PathVariable long id, Post updatedPost,
            @RequestAttribute MultipartFile postImage, @RequestParam(value = "sections", required = false) List<Long> sectionIds, @RequestParam("contributors") List<String> contributors)
            throws IOException {
        Optional<Post> op = postService.findPostById(id);

        if (op.isPresent()) {
            if (sectionIds != null) {
                for (long sectionId : sectionIds) {
                    updatedPost.addSection(sectionService.findById(sectionId).get());
                }
            }
            for (String colaborator : contributors) {
                if (userService.findByUserName(colaborator) != null) {
                    updatedPost.addContributor(userService.findByUserName(colaborator));
                }
            }


            postService.updatePost(op.get(), updatedPost, postImage);
            return "redirect:/post/" + id;

        } else {
            model.addAttribute("message", "No se ha encontrado un post con ese nombre");
            return "error";
        }
    }

    @PostMapping("/post/{id}/delete")
    public String deletePost(@PathVariable long id, Model model) {
        Optional<Post> op = postService.findPostById(id);
        if (op.isPresent()) {
            postService.deletePost(op.get());
            return "redirect:/post";
        } else {
            model.addAttribute("message", "No se ha encontrado un post con ese nombre");
            return "error";
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
            if (newComment.getContent().isEmpty()) {
                model.addAttribute("message", "El comentario no puede estar vacio");
                return "error";

            } else if (newComment.getRating() > 5 || newComment.getRating() < 0) {
                model.addAttribute("message", "El valor del rating debe estar entre 0 y 5");
                return "error";
            }
            commentService.saveCommentInPost(op.get(), newComment);
            return "redirect:/post/" + postId;

        } else {
            model.addAttribute("message", "No se han encontrado el post o comentario especificados");
            return "error";
        }

    }

    // this should work, but the user can delete a comment from a post that is not
    // on that post (manipulating the request?), need to be implemented a checker
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
            return "error";
        }
    }

    @PostMapping("/post/{postId}/comment/{commentId}/edit")
    public String editPostCommentInfo(@PathVariable long postId, @PathVariable long commentId, Model model,
            Comment updatedComment) {
        Optional<Post> op = postService.findPostById(postId);
        Optional<Comment> opComment = commentService.findCommentById(commentId);

        if (op.isPresent() && opComment.isPresent()) {
            if (updatedComment.getContent().isEmpty()) {
                model.addAttribute("message", "El comentario no puede estar vacio");
                return "error";

            } else if (updatedComment.getRating() > 5 || updatedComment.getRating() < 0) {
                model.addAttribute("message", "El valor del rating debe estar entre 0 y 5");
                return "error";
            }
            commentService.updateComment(commentId, updatedComment, op.get());
            return "redirect:/post/" + postId;

        } else {
            model.addAttribute("message", "No se han encontrado el post o comentario especificados");
            return "error";
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
            model.addAttribute("message", "No se ha encontrado el post o comentario especificado");
            return "error";
        }
    }

}
