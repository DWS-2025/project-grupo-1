package es.codeurjc.web.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.dto.CommentDTO;
import es.codeurjc.web.dto.CreateCommentDTO;
import es.codeurjc.web.dto.CreatePostDTO;
import es.codeurjc.web.dto.PostDTO;
import es.codeurjc.web.service.CommentService;
import es.codeurjc.web.service.PostService;
import es.codeurjc.web.service.SectionService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller class for managing posts and their related operations in the web application.
 * <p>
 * Handles HTTP requests for creating, viewing, editing, and deleting posts, as well as
 * managing comments associated with posts. Also provides endpoints for image download
 * and section management within posts.
 * </p>
 *
 * <ul>
 *   <li>GET /post - List all posts</li>
 *   <li>GET /post/new - Show form to create a new post</li>
 *   <li>POST /post/new - Handle creation of a new post</li>
 *   <li>GET /post/{postId} - View a specific post and its comments</li>
 *   <li>GET /post/{postId}/image - Download the image associated with a post</li>
 *   <li>GET /post/{postId}/edit - Show form to edit a post (owner only)</li>
 *   <li>POST /post/{postId}/edit - Handle post update (owner only)</li>
 *   <li>POST /post/{postId}/delete - Delete a post (owner only)</li>
 *   <li>GET /post/{postId}/comment/new - Show form to add a comment to a post</li>
 *   <li>POST /post/{postId}/comment/new - Handle creation of a new comment</li>
 *   <li>GET /post/{postId}/comment/{commentId}/edit - Show form to edit a comment (owner only)</li>
 *   <li>POST /post/{postId}/comment/{commentId}/edit - Handle comment update (owner only)</li>
 *   <li>POST /post/{postId}/comment/{commentId}/delete - Delete a comment (owner only)</li>
 * </ul>
 *
 * <p>
 * Access control is enforced for editing and deleting posts and comments, ensuring only
 * owners can perform these actions. Error messages are provided for unauthorized actions
 * or invalid input.
 * </p>
 * 
 * @author Grupo 1
 */
@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SectionService sectionService;

    @GetMapping("/post")
    public String viewPosts(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("posts", postService.findAllAsDTO());

        return "post_list";

    }

    @GetMapping("/post/new")
    public String createPost(Model model) {
        model.addAttribute("sections", sectionService.findAll());
        model.addAttribute("isEditing", false);

        return "post_form";
    }

    @PostMapping("/post/new")
    public String createPost(Model model, @ModelAttribute CreatePostDTO createPostDTO,
            @RequestParam MultipartFile newImage, @RequestParam String newContributors,
            @RequestParam(value = "sections", required = false) List<Long> sectionIds, HttpServletRequest request)
            throws IOException {

        if (createPostDTO.title().isEmpty()) {
            model.addAttribute("message", "The title cannot be empty");
            return "error";
        }

        postService.save(createPostDTO, newImage, sectionIds, newContributors.split(","), request);

        return "redirect:/post";

    }

    @GetMapping("/post/{postId}")
    public String viewPost(Model model, @PathVariable Long postId, HttpServletRequest request) {
        PostDTO postDTO = postService.findByIdAsDTO(postId);

        if (request.getUserPrincipal() != null) {

            model.addAttribute("post", postDTO);
            model.addAttribute("comments", commentService.findAllCommentsByPostId(postId));
            model.addAttribute("currentPage", 0); // commentPage.getNumber());
            model.addAttribute("hasImage", postDTO.image() != null);
            model.addAttribute("isOwner", postService.checkIfUserIsTheOwner(postId, request));
            model.addAttribute("logged", true);

            return "view_post";

        } else {

            model.addAttribute("post", postDTO);
            model.addAttribute("comments", commentService.findAllCommentsByPostId(postId));
            model.addAttribute("currentPage", 0); // commentPage.getNumber());
            model.addAttribute("hasImage", postDTO.image() != null);
            model.addAttribute("logged", false);

            return "view_post";

        }

    }

    @GetMapping("/post/{postId}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable Long postId) throws SQLException {

        return postService.getImageFileFromId(postId);

    }

    @GetMapping("/post/{postId}/edit")
    public String updatePost(Model model, @PathVariable Long postId, HttpServletRequest request) {

        if (postService.checkIfUserIsTheOwner(postId, request)) {

            PostDTO postDTO = postService.findByIdAsDTO(postId);

            List<Map<String, Object>> markedSections = postService.preparePostSectionsForForm(postId);

            String contributors = postService.contributorsToString(postId);

            model.addAttribute("sections", markedSections);
            model.addAttribute("post", postDTO);
            model.addAttribute("title", postDTO.title());
            model.addAttribute("content", postDTO.content());
            model.addAttribute("contributors", contributors);
            model.addAttribute("isEditing", true);

            return "post_form";

        } else {
            model.addAttribute("message", "You are not allowed to do this");
            return "error";
        }

    }

    @PostMapping("/post/{postId}/edit")
    public String updatePost(Model model, @PathVariable Long postId, @ModelAttribute CreatePostDTO createPostDTO,
            @RequestParam MultipartFile newImage,
            @RequestParam(value = "sections", required = false) List<Long> newSectionIds,
            @RequestParam String newContributors, HttpServletRequest request) throws IOException {

        if (postService.checkIfUserIsTheOwner(postId, request)) {

            postService.updatePost(postId, createPostDTO, newImage, newSectionIds, newContributors.split(","), request);
            return "redirect:/post/" + postId;

        } else {
            model.addAttribute("message", "You are not allowed to do this");
            return "error";
        }

    }

    @PostMapping("/post/{postId}/delete")
    public String deletePost(@PathVariable Long postId, Model model, HttpServletRequest request) {

        if (postService.checkIfUserIsTheOwner(postId, request)) {

            postService.deletePost(postId);
            return "redirect:/post";

        } else {
            model.addAttribute("message", "You are not allowed to do this");
            return "error";
        }

    }

    @GetMapping("/post/{postId}/comment/new")
    public String newPostCommentForm(Model model, @PathVariable Long postId) {
        model.addAttribute("post", postService.findByIdAsDTO(postId));
        return "comment_form";

    }

    @PostMapping("/post/{postId}/comment/new")
    public String newPostComment(Model model, @PathVariable Long postId, CreateCommentDTO newComment,
            HttpServletRequest request) {

        if (postService.existsById(postId)) {

            if (newComment.content().isEmpty()) {
                model.addAttribute("message", "El comentario no puede estar vacio");
                return "error";

            } else if (newComment.rating() > 5 || newComment.rating() < 0) {
                model.addAttribute("message", "El valor del rating debe estar entre 0 y 5");
                return "error";
            }

            commentService.saveCommentInPost(postId, newComment, request);
            return "redirect:/post/" + postId;

        } else {
            model.addAttribute("message", "El post no existe");
            return "error";
        }

    }

    @GetMapping("/post/{postId}/comment/{commentId}/edit")
    public String editPostComment(@PathVariable Long postId, @PathVariable Long commentId, Model model,
            HttpServletRequest request) {

        if (commentService.checkIfCommentOwnerAndCommnetOnPost(request, postId, commentId)) {

            model.addAttribute("post", postService.findByIdAsDTO(postId));
            model.addAttribute("comment", commentService.findCommentByIdDTO(commentId));
            model.addAttribute("isEditing", true);

            return "comment_form";

        } else {
            model.addAttribute("message",
                    "No tienes permisos para realizar esa acción, o estás intentando editar un comentario de otro post");
            return "error";
        }

    }

    @PostMapping("/post/{postId}/comment/{commentId}/edit")
    public String editPostCommentInfo(@PathVariable Long postId, @PathVariable Long commentId, Model model,
            CommentDTO updatedComment, HttpServletRequest request) {

        if (commentService.checkIfCommentOwnerAndCommnetOnPost(request, postId, commentId)) {

            postService.findByIdAsDTO(postId);
            commentService.findCommentByIdDTO(commentId);

            if (updatedComment.content().isEmpty()) {
                model.addAttribute("message", "El comentario no puede estar vacio");
                return "error";

            } else if (updatedComment.rating() > 5 || updatedComment.rating() < 0) {
                model.addAttribute("message", "El valor del rating debe estar entre 0 y 5");
                return "error";
            }

            commentService.updateComment(commentId, updatedComment, postId);

            return "redirect:/post/" + postId;

        } else {
            model.addAttribute("message",
                    "No tienes permisos para realizar esa acción, o estás intentando editar un comentario de otro post");
            return "error";
        }

    }

    @PostMapping("/post/{postId}/comment/{commentId}/delete")
    public String deletePostComment(@PathVariable Long postId, @PathVariable Long commentId, Model model,
            HttpServletRequest request) {

        if (commentService.checkIfCommentOwnerAndCommnetOnPost(request, postId, commentId)) {

            PostDTO postDTO = postService.findByIdAsDTO(postId);
            commentService.findCommentByIdDTO(commentId);
            commentService.deleteCommentFromPost(postId, commentId);

            model.addAttribute("post", postDTO);
            model.addAttribute("Comments", postDTO.comments());

            return "redirect:/post/" + postId;

        } else {
            model.addAttribute("message",
                    "No tienes permisos para realizar esa acción, o estás intentando eliminar un comentario de otro post");
            return "error";

        }

    }

}
