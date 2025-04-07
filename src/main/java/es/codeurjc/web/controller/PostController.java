package es.codeurjc.web.controller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.dto.CommentDTO;
import es.codeurjc.web.dto.PostDTO;
import es.codeurjc.web.dto.UserDTO;
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
        model.addAttribute("posts", postService.findAll());
        return "post_list";
    }

    @GetMapping("/post/new")
    public String createPost(Model model) {
        model.addAttribute("sections", sectionService.findAll());
        model.addAttribute("isEditing", false);
        System.out.println("=======================================\n\n\nGET NEW\n\n\n=======================================");
        return "post_form";
    }

    @PostMapping("/post/new")
    public String createPost(Model model, Post post, @RequestParam MultipartFile newImage, @RequestParam String newContributors, @RequestParam(value = "sections", required = false) List<Long> sectionIds) throws IOException {  
        
        postService.addSections(post, sectionIds);
        
        String[] contributorsArray = newContributors.split(",");
        for (String colaborator : contributorsArray) {
            UserDTO user = userService.findByUserName(colaborator);
            if (user != null) {
                post.addContributor(user);
            }
        }
        System.out.println("=======================================\n\n\nPRESAVE\n\n\n=======================================");
        postService.save(post, newImage);
        return "redirect:/post";
    }

    @GetMapping("/post/{id}")
    public String viewPost(Model model, @PathVariable long id, @RequestParam(defaultValue = "0") int page) {
        Optional<PostDTO> op = postService.findByIdDTO(id);
        if (op.isPresent()) {   

            Page<CommentDTO> commentPage = commentService.findAllCommentsByPostId(id,page);

            model.addAttribute("post", op.get());
            model.addAttribute("comments", commentService.findAllCommentsByPostId(id,page).getContent());
            model.addAttribute("currentPage", commentPage.getNumber());
            model.addAttribute("hasImage", op.get().postImage() != null);
            return "view_post";

        } else {
            model.addAttribute("message", "No se ha encontrado un post con ese nombre");
            return "error";
        }
    }

    @GetMapping("/post/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        
        Optional<Post> op = postService.findById(id);
        
        if (op.isPresent() && op.get().getPostImage() != null) {
            Blob image = op.get().getPostImage();
            Resource file = new InputStreamResource(image.getBinaryStream());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").contentLength(image.length()).body(file);
        
        } else {
            return ResponseEntity.notFound().build();
        
        }

    }

    @GetMapping("/post/{id}/edit")
    public String updatePost(Model model, @PathVariable long id) {
        Optional<Post> op = postService.findById(id);
        if (op.isPresent()) {
            Post post = op.get();

            Collection<Section> allSections = sectionService.findAll();
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
                contributors += user.getUserName() + ",";
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
    public String updatePost(Model model, @PathVariable long id, @RequestParam String title, @RequestParam String content,
            @RequestAttribute MultipartFile newImage, @RequestParam(value = "sections", required = false) List<Long> newSectionIds, @RequestParam("newContributors") String newContributorsStrings)
            throws IOException {
        Optional<Post> op = postService.findById(id);

        if (op.isPresent()) {



            postService.updatePost(op.get(), title, content, newSectionIds, newContributorsStrings.split(","), newImage);
            return "redirect:/post/" + id;

        } else {
            model.addAttribute("message", "No se ha encontrado un post con ese nombre");
            return "error";
        }
    }

    @PostMapping("/post/{id}/delete")
    public String deletePost(@PathVariable long id, Model model) {
        Optional<Post> op = postService.findById(id);
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
        Optional<PostDTO> op = postService.findByIdDTO(postId);
        if (op.isPresent()) {
            model.addAttribute("post", op.get());
            return "comment_form";
        } else {
            model.addAttribute("message", "No se ha encontrado un post con ese nombre");
            return "error";
        }
    }

    @PostMapping("/post/{postId}/comment/new")
    public String newPostComment(Model model, @PathVariable long postId, CommentDTO newComment) {
        Optional<PostDTO> op = postService.findByIdDTO(postId);
        if (op.isPresent()) {
            if (newComment.content().isEmpty()) {
                model.addAttribute("message", "El comentario no puede estar vacio");
                return "error";

            } else if (newComment.rating() > 5 || newComment.rating() < 0) {
                model.addAttribute("message", "El valor del rating debe estar entre 0 y 5");
                return "error";
            }
            commentService.saveCommentInPost(postId, newComment);
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
        Optional<PostDTO> op = postService.findByIdDTO(postId);
        Optional<CommentDTO> opComment = commentService.findCommentByIdDTO(commentId);

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
            CommentDTO updatedComment) {
        Optional<PostDTO> op = postService.findByIdDTO(postId);
        Optional<CommentDTO> opComment = commentService.findCommentByIdDTO(commentId);

        if (op.isPresent() && opComment.isPresent()) {
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
            model.addAttribute("message", "No se han encontrado el post o comentario especificados");
            return "error";
        }

    }

    @PostMapping("/post/{postId}/comment/{commentId}/delete")
    public String deletePostComment(@PathVariable long postId, @PathVariable long commentId, Model model) {
        Optional<PostDTO> op = postService.findByIdDTO(postId);
        Optional<CommentDTO> opComment = commentService.findCommentByIdDTO(commentId);

        if (op.isPresent() && opComment.isPresent()) {
            commentService.deleteCommentFromPost(postId, commentId);
            model.addAttribute("post", op.get());
            model.addAttribute("Comments", op.get().comments());
            return "redirect:/post/" + postId;

        } else {
            model.addAttribute("message", "No se ha encontrado el post o comentario especificado");
            return "error";
        }
    }

}
