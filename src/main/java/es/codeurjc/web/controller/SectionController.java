package es.codeurjc.web.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.codeurjc.web.model.Section;
import es.codeurjc.web.repository.CommentRepository;
import es.codeurjc.web.repository.PostRepository;
import es.codeurjc.web.service.*;

@Controller
public class SectionController {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;


    @GetMapping("/section")
    public String showSections(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 6;

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Section> sectionsPage = sectionService.findAll(pageable);

        model.addAttribute("sections", sectionsPage.getContent());
        model.addAttribute("hasPrev", sectionsPage.hasPrevious());
        model.addAttribute("hasNext", sectionsPage.hasNext());
        model.addAttribute("prev", page - 1);
        model.addAttribute("next", page + 1);

        return "section";
    }

    @GetMapping("/section/new")
    public String createSection(Model model) {
        return "create_section";
    }

    /*
     * @PostMapping("/section/new")
     * public String createSection(@RequestParam String title, @RequestParam String
     * description,
     * 
     * @RequestParam MultipartFile sectionImage) throws IOException {
     * 
     * Section section = new Section(title, description, null);
     * sectionService.saveSection(section);
     * 
     * imageSectionService.saveImage(SECTIONS_FOLDER, section.getId(),
     * sectionImage);
     * String imageName = sectionImage.getOriginalFilename();
     * section.setSectionImage(imageName);
     * 
     * sectionService.saveSection(section);
     * 
     * return "redirect:/section";
     * }
     */

    /*
     * @PostMapping("/section/new")
     * public String createSection(Model model, Section section, MultipartFile
     * sectionImage) throws Exception{
     * sectionService.saveImageSection(section, sectionImage);
     * return "redirect:/section";
     * }
     */

    @PostMapping("/section/new")
    public String createSection(@RequestParam String title, @RequestParam String description,
            @RequestParam MultipartFile sectionImage) {
        try {
            Section section = new Section(title, description);
            sectionService.saveSectionWithImageSection(section, sectionImage);
            return "redirect:/section";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    /*
     * @GetMapping("/section/{id}/image")
     * public ResponseEntity<Object> downloadImage(@PathVariable long id) throws
     * MalformedURLException {
     * return imageSectionService.createResponseFromImage(SECTIONS_FOLDER, id);
     * }
     */

    @GetMapping("/section/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        Optional<Section> op = sectionService.findById(id);

        if (op.isPresent() && op.get().getSectionImage() != null) {
            Blob image = op.get().getSectionImage();
            Resource file = new InputStreamResource(image.getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").contentLength(image.length())
                    .body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/section/{id}/delete")
    public String deleteSection(Model model, @PathVariable long id) {
        Optional<Section> section = sectionService.findById(id);

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
        Optional<Section> section = sectionService.findById(id);

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
        Optional<Section> section = sectionService.findById(id);

        if (section.isPresent()) {
            model.addAttribute("section", section.get());
            return "view_section";
        } else {
            model.addAttribute("message", "No se ha encontrado una sección con ese nombre");
            return "error";
        }

    }

    @GetMapping("/section/{id}/unfollow")
    public String unfollowSection(Model model, @PathVariable long id) {
        Optional<Section> section = sectionService.findById(id);

        if (section.isPresent()) {
            userService.getLoggedUser().unfollowSection(section.get());
            return "redirect:/following";
        } else {
            model.addAttribute("message", "No se ha encontrado una sección con ese nombre");
            return "error";
        }

    }

    @GetMapping("/section/{id}/follow")
    public String followSection(Model model, @PathVariable long id) {
        Optional<Section> section = sectionService.findById(id);

        if (section.isPresent()) {
            userService.getLoggedUser().followSection(section.get());
            return "redirect:/discover";
        } else {
            model.addAttribute("message", "No se ha encontrado una sección con ese nombre");
            return "error";
        }

    }

    @GetMapping("/section/{id}/edit") 
    public String editSection(Model model, @PathVariable long id) {
        Optional<Section> section = sectionService.findById(id);

        if (section.isPresent()) {
            model.addAttribute("section", section.get());
            return "editSection";
        } else {
            model.addAttribute("message", "No se ha encontrado una sección con ese nombre");
            return "error";
        }

    }

    @PostMapping("/section/{id}/edit")
    public String updateSection(RedirectAttributes redirectAttributes, Model model, @PathVariable long id, Section updatedSection, MultipartFile newImage) throws IOException {
        Optional<Section> op = sectionService.findById(id);

        if (op.isPresent()) {
            Section oldSection = op.get();
            sectionService.update(oldSection, updatedSection, newImage);
            redirectAttributes.addFlashAttribute("successMessage", "La sección se ha editado correctamente.");
            return "redirect:/section";
        } else {
            model.addAttribute("message", "No se ha encontrado una sección con ese nombre");
            return "error";
        }

    }

    @GetMapping("/section/search")
    public String searchSection(Model model, @RequestParam(required = false) String title) {
        if (title != null) {
            Section section = new Section();
            section.setTitle(title);

            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).withIgnoreCase()
                    .withIgnorePaths("id", "averageRating", "numberOfPublications", "posts", "author", "sectionImage");
            Example<Section> example = Example.of(section, matcher);

            if(sectionService.findAll(example).isEmpty()){
                model.addAttribute("noResult", true);
                model.addAttribute("sections", sectionService.findAll());
            }
            else{
                model.addAttribute("sections", sectionService.findAll(example));
                model.addAttribute("isSearch", true);
            }
        } else {
            model.addAttribute("sections", sectionService.findAll());
            
        }
        return "section";

    }

}
