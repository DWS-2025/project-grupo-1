package es.codeurjc.web.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import es.codeurjc.web.dto.CreateCommentDTO;
import es.codeurjc.web.dto.CreatePostDTO;
import es.codeurjc.web.dto.PostDTO;
import es.codeurjc.web.service.CommentService;
import es.codeurjc.web.service.PostService;
import es.codeurjc.web.service.SectionService;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;


@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;

    @GetMapping("/post")
    public String viewPosts(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10; // Number of posts per page
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PostDTO> postPage = postService.findAllAsDTO(pageable);

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", postPage.getNumber());
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("hasPrev", postPage.hasPrevious());
        model.addAttribute("hasNext", postPage.hasNext());
        model.addAttribute("prev", page - 1);
        model.addAttribute("next", page + 1);
        model.addAttribute("currentPage", page);

        return "post_list";

    }

    @GetMapping("/post/new")
    public String createPost(Model model) {
        model.addAttribute("sections", sectionService.findAll());
        model.addAttribute("isEditing", false);
        
        return "post_form";
    }

    @PostMapping("/post/new")
    public String createPost(Model model, CreatePostDTO createPostDTO, @RequestParam MultipartFile newImage, @RequestParam String newContributors, @RequestParam(value = "sections", required = false) List<Long> sectionIds, HttpServletRequest request) throws IOException {  
        
        postService.addSections(createPostDTO, sectionIds);
        
        String[] contributorsArray = newContributors.split(",");
        postService.addContributors(createPostDTO, contributorsArray);
        postService.save(createPostDTO, newImage, request);
        return "redirect:https://localhost:8443/post";
    }
    

    @GetMapping("/post/{id}")
    public String viewPost(Model model, @PathVariable long id) {//, @RequestParam(defaultValue = "0") int page) {
        PostDTO postDTO = postService.findByIdAsDTO(id);
        //Page<CommentDTO> commentPage = commentService.findAllCommentsByPostId(id,page);

        model.addAttribute("post", postDTO);
        model.addAttribute("comments", commentService.findAllCommentsByPostId(id));
        //model.addAttribute("comments", commentService.findAllCommentsByPostId(id,page).getContent());
        model.addAttribute("currentPage", 0); //commentPage.getNumber());
        model.addAttribute("hasImage", postDTO.image() != null);
        return "view_post";

    }

    @GetMapping("/post/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

        return postService.getImageFileFromId(id);
        
    }


    @GetMapping("/post/{id}/edit")
    public String updatePost(Model model, @PathVariable long id) {
        
        PostDTO postDTO = postService.findByIdAsDTO(id);

        List<Map<String, Object>> markedSections = postService.preparePostSectionsForForm(postDTO);

            // Create a List<Section> with "selected" property
            // List<Map<String, Object>> sectionsWithSelection = new ArrayList<>();
            // for (Section section : allSections) {
            //     Map<String, Object> sectionData = new HashMap<>();
            //     sectionData.put("id", section.getId());
            //     sectionData.put("title", section.getTitle());

            //     // Verify if the section is in the post using its id
            //     boolean isSelected = postSections.stream()
            //             .anyMatch(s -> s.getId() == section.getId());

            //     sectionData.put("selected", isSelected);
            //     sectionsWithSelection.add(sectionData);
            // }

        String contributors = postService.contributorsToString(postDTO);

        model.addAttribute("sections", markedSections);
        model.addAttribute("post", postDTO);
        model.addAttribute("title", postDTO.title());
        model.addAttribute("content", postDTO.content());
        model.addAttribute("contributors", contributors);
        model.addAttribute("isEditing", true);
        return "post_form";

    }

    @PostMapping("/post/{id}/edit")
    public String updatePost(Model model, @PathVariable Long id, CreatePostDTO createPostDTO,
            @RequestAttribute MultipartFile newImage, @RequestParam(value = "sections", required = false) List<Long> newSectionIds, @RequestParam("newContributors") String newContributorsStrings)
            throws IOException {

            postService.updatePost(id, createPostDTO, newSectionIds, newContributorsStrings.split(","), newImage);
            return "redirect:/post/" + id;

    }

    @PostMapping("/post/{id}/delete")
    public String deletePost(@PathVariable long id, Model model) {
        
        PostDTO postDTO = postService.findByIdAsDTO(id);
        postService.deletePost(postDTO);
        return "redirect:/post";

    }

    @GetMapping("/post/{postId}/comment/new")
    public String newPostCommentForm(Model model, @PathVariable long postId) {
            model.addAttribute("post", postService.findByIdAsDTO(postId));
            return "comment_form";

    }

    @PostMapping("/post/{postId}/comment/new")
    public String newPostComment(Model model, @PathVariable long postId, CreateCommentDTO newComment, HttpServletRequest request) {
        postService.findByIdAsDTO(postId);
            
        if (newComment.content().isEmpty()) {
            model.addAttribute("message", "El comentario no puede estar vacio");
            return "error";

        } else if (newComment.rating() > 5 || newComment.rating() < 0) {
            model.addAttribute("message", "El valor del rating debe estar entre 0 y 5");
            return "error";
        }

        commentService.saveCommentInPost(postId, newComment, request);
        return "redirect:/post/" + postId;

    }

    // this should work, but the user can delete a comment from a post that is not
    // on that post (manipulating the request?), need to be implemented a checker
    @GetMapping("/post/{postId}/comment/{commentId}/edit")
    public String editPostComment(@PathVariable long postId, @PathVariable long commentId, Model model) {

        model.addAttribute("post", postService.findByIdAsDTO(postId));
        model.addAttribute("comment", commentService.findCommentByIdDTO(commentId));
        model.addAttribute("isEditing", true);
        return "comment_form";

    }

    @PostMapping("/post/{postId}/comment/{commentId}/edit")
    public String editPostCommentInfo(@PathVariable long postId, @PathVariable long commentId, Model model,
            CommentDTO updatedComment) {
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

    }

    @PostMapping("/post/{postId}/comment/{commentId}/delete")
    public String deletePostComment(@PathVariable long postId, @PathVariable long commentId, Model model) {

        PostDTO postDTO = postService.findByIdAsDTO(postId);
        commentService.findCommentByIdDTO(commentId);

        commentService.deleteCommentFromPost(postId, commentId);
        model.addAttribute("post", postDTO);
        model.addAttribute("Comments", postDTO.comments());
        return "redirect:/post/" + postId;

    }

}