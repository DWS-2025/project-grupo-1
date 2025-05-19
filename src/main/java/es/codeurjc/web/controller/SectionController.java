package es.codeurjc.web.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.codeurjc.web.dto.CreateSectionDTO;
import es.codeurjc.web.dto.SectionDTO;
import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.service.SectionService;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller for managing Section-related operations in the web application.
 * <p>
 * Handles requests for viewing, creating, editing, deleting, following, and unfollowing sections.
 * Also manages section image downloads and section search/filtering.
 * </p>
 * 
 * <ul>
 *   <li>Injects {@link SectionService} and {@link UserService} for business logic.</li>
 *   <li>Adds user authentication and role attributes to the model for each request.</li>
 *   <li>Supports pagination for section listing.</li>
 *   <li>Handles multipart file uploads for section images.</li>
 *   <li>Provides endpoints for section CRUD operations and user-section interactions.</li>
 *   <li>Supports advanced filtering and searching of sections.</li>
 * </ul>
 * 
 * Endpoints:
 * <ul>
 *   <li><b>GET /section</b>: List sections with pagination.</li>
 *   <li><b>GET /section/new</b>: Show form to create a new section.</li>
 *   <li><b>POST /section/new</b>: Create a new section with optional image upload.</li>
 *   <li><b>GET /section/{id}/image</b>: Download section image.</li>
 *   <li><b>POST /section/{id}/delete</b>: Delete a section (POST method).</li>
 *   <li><b>GET /section/{id}/delete</b>: Delete a section (GET method).</li>
 *   <li><b>GET /section/{id}</b>: View section details.</li>
 *   <li><b>GET /section/{id}/follow</b>: Follow a section.</li>
 *   <li><b>GET /section/{id}/unfollow</b>: Unfollow a section.</li>
 *   <li><b>GET /section/{id}/edit</b>: Show form to edit a section.</li>
 *   <li><b>POST /section/{id}/edit</b>: Update section details and image.</li>
 *   <li><b>GET /section/search</b>: Search and filter sections by various criteria.</li>
 * </ul>
 * 
 * Security:
 * <ul>
 *   <li>Checks user authentication and roles for certain operations.</li>
 *   <li>Handles error cases and redirects appropriately.</li>
 * </ul>
 * 
 * @author Grupo 1
 */
@Controller
public class SectionController {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {

            model.addAttribute("logged", true);
            model.addAttribute("userName", principal.getName());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));

        } else {
            model.addAttribute("logged", false);
        }
    }

    @GetMapping("/section")
    public String showSections(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<SectionDTO> sectionsPage = sectionService.findAllAsDTO(pageable);

        model.addAttribute("sections", sectionsPage.getContent());
        model.addAttribute("hasPrev", sectionsPage.hasPrevious());
        model.addAttribute("hasNext", sectionsPage.hasNext());
        model.addAttribute("prev", page - 1);
        model.addAttribute("next", page + 1);
        model.addAttribute("currentPage", page);

        return "section";
    }

    @GetMapping("/section/new")
    public String createSection(Model model) {
        return "create_section";
    }

    @PostMapping("/section/new")
    public String createSection(@ModelAttribute CreateSectionDTO sectionDTO, @RequestParam MultipartFile sectionImage) {
        try {
            sectionService.saveSectionWithImageSection(sectionDTO, sectionImage);
            return "redirect:/section";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/section/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        Optional<Section> op = sectionService.findSectionById(id);

        if (op.isPresent() && op.get().getImageFile() != null) {
            Blob image = op.get().getImageFile();
            Resource file = new InputStreamResource(image.getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").contentLength(image.length())
                    .body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/section/{id}/delete")
    public String deleteSection(Model model, @PathVariable long id) {
        Optional<SectionDTO> section = sectionService.findById(id);

        if (section.isPresent()) {
            sectionService.deleteSection(section.get());
            model.addAttribute("byPost", true);

            return "delete_section";

        } else {
            return "redirect:/section";
        }
    }

    @GetMapping("/section/{id}/delete")
    public String deleteSectionG(Model model, @PathVariable long id) {
        Optional<SectionDTO> section = sectionService.findById(id);

        if (section.isPresent()) {
            sectionService.deleteSection(section.get());
            return "delete_section";

        } else {
            model.addAttribute("message", "No se ha encontrado una sección con ese nombre");
            return "error";
        }

    }

    @GetMapping("/section/{id}")
    public String viewSection(Model model, @PathVariable long id) {
        Optional<SectionDTO> section = sectionService.findById(id);

        if (section.isPresent()) {
            model.addAttribute("section", section.get());
            return "view_section";
        } else {
            model.addAttribute("message", "No se ha encontrado una sección con ese nombre");
            return "error";
        }

    }

    @GetMapping("/section/{id}/unfollow")
    public String unfollowSection(Model model, @PathVariable long id, HttpServletRequest request) {
        Optional<SectionDTO> section = sectionService.findById(id);
        UserDTO userDTO = userService.getLoggedUser(request.getUserPrincipal().getName());

        if (section.isPresent()) {
            userService.unfollowSection(userDTO, section.get());
            return "redirect:/following";
        } else {
            model.addAttribute("message", "No se ha encontrado una sección con ese nombre");
            return "error";
        }

    }

    @GetMapping("/section/{id}/follow")
    public String followSection(Model model, @PathVariable long id, HttpServletRequest request) {
        Optional<SectionDTO> section = sectionService.findById(id);
        UserDTO userDTO = userService.getLoggedUser(request.getUserPrincipal().getName());

        if (section.isPresent()) {
            userService.followSection(userDTO, section.get());
            return "redirect:/discover";
        } else {
            model.addAttribute("message", "No se ha encontrado una sección con ese nombre");
            return "error";
        }

    }

    @GetMapping("/section/{id}/edit")
    public String editSection(Model model, @PathVariable long id) {
        Optional<SectionDTO> section = sectionService.findById(id);

        if (section.isPresent()) {
            model.addAttribute("section", section.get());
            return "editSection";
        } else {
            model.addAttribute("message", "No se ha encontrado una sección con ese nombre");
            return "error";
        }

    }

    @PostMapping("/section/{id}/edit")
    public String updateSection(RedirectAttributes redirectAttributes, Model model, @PathVariable long id,
            SectionDTO updatedSection, MultipartFile newImage) throws IOException {
        Optional<SectionDTO> op = sectionService.findById(id);

        if (op.isPresent()) {
            SectionDTO oldSection = op.get();
            sectionService.update(oldSection, updatedSection, newImage);
            redirectAttributes.addFlashAttribute("successMessage", "La sección se ha editado correctamente.");
            return "redirect:/section";
        } else {
            model.addAttribute("message", "No se ha encontrado una sección con ese nombre");
            return "error";
        }

    }

    @GetMapping("/section/search")
    public String searchSection(Model model, @RequestParam(required = false) List<String> filters) {
        Collection<SectionDTO> sections;

        if (filters == null || filters.isEmpty()) {
            sections = sectionService.getAllSections();
        } else {
            if (filters.contains("title") && !filters.contains("minPosts") && !filters.contains("minRating")) {
                sections = sectionService.getSectionByTitltesASC();
            } else if (filters.contains("minPosts") && !filters.contains("title") && !filters.contains("minRating")) {
                sections = sectionService.getSectionPublicationsGT2();
            } else if (filters.contains("minRating") && !filters.contains("minPosts") && !filters.contains("title")) {
                sections = sectionService.getSectionAverageRatingGT5();
            } else if (filters.contains("minRating") && filters.contains("minPosts") && !filters.contains("title")) {
                sections = sectionService.getSectionAverageRatingGT5PublicationsGTE2();
            } else if (filters.contains("title") && filters.contains("minPosts") && filters.contains("minRating")) {
                sections = sectionService.getSectionPostsGTE2AverageRatingGT5();
            } else if (filters.contains("title") && filters.contains("minRating") && !filters.contains("minPosts")) {
                sections = sectionService.getSectionAverageRatingGTE5ByTitle();
            } else if (filters.contains("title") && filters.contains("minPosts") && !filters.contains("minRating")) {
                sections = sectionService.getSectionPostsGTE2ByTitle();
            } else {
                sections = sectionService.getAllSections();
            }
        }

        model.addAttribute("sections", sections);
        return "section";
    }

}
