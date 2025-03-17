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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.Section;
import es.codeurjc.web.repository.CommentRepository;
import es.codeurjc.web.service.*;

@Controller
public class SectionController {

    private final ImageUserService imageUserService;

    private final ImagePostService imagePostService;

    private final CommentService commentService;

    private final CommentRepository commentRepository;
    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageSectionService imageSectionService;

    private static final String SECTIONS_FOLDER = "sections";

    SectionController(CommentRepository commentRepository, CommentService commentService,
            ImagePostService imagePostService, ImageUserService imageUserService) {
        this.commentRepository = commentRepository;
        this.commentService = commentService;
        this.imagePostService = imagePostService;
        this.imageUserService = imageUserService;
    }

    @GetMapping("/section")
    public String showSections(Model model) {
        List<Section> sections = sectionService.findAll();
        model.addAttribute("sections", sections);

        boolean islogged = userService.isLogged(userService.getLoggedUser());
        model.addAttribute("islogged", islogged);

        return "section";
    }

    @GetMapping("/section/new")
    public String createSection(Model model) {
        return "create_section";
    }

   /* @PostMapping("/section/new")
    public String createSection(@RequestParam String title, @RequestParam String description,
            @RequestParam MultipartFile sectionImage) throws IOException {

        Section section = new Section(title, description, null);
        sectionService.saveSection(section);

        imageSectionService.saveImage(SECTIONS_FOLDER, section.getId(), sectionImage);
        String imageName = sectionImage.getOriginalFilename();
        section.setSectionImage(imageName);

        sectionService.saveSection(section);

        return "redirect:/section";
    }
    */

   /* @PostMapping("/section/new")
    public String createSection(Model model, Section section, MultipartFile sectionImage) throws Exception{
        sectionService.saveImageSection(section, sectionImage);
        return "redirect:/section";
    }*/ 


    @PostMapping("/section/new")
    public String createSection(@RequestParam String title, @RequestParam String description, @RequestParam MultipartFile sectionImage) {
        try {
            Section section = new Section(title, description, null);
            sectionService.saveImageSection(section, sectionImage);
            return "redirect:/section";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

   /* @GetMapping("/section/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws MalformedURLException {
        return imageSectionService.createResponseFromImage(SECTIONS_FOLDER, id);
    } */

    @GetMapping("/section/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        Optional<Section> op = sectionService.findById(id);

        if(op.isPresent() && op.get().getSectionImage() != null){
            Blob image = op.get().getSectionImage();
            Resource file = new InputStreamResource(image.getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").contentLength(image.length()).body(file);
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

    @GetMapping("/section/{id}/edit") //hacer la pagina
    public String editSection(Model model, @PathVariable long id) {
        Optional<Section> section = sectionService.findById(id);

        if (section.isPresent()) {
            model.addAttribute("section", section.get());
            return "edit_section";
        } else {
            model.addAttribute("message", "No se ha encontrado una sección con ese nombre");
            return "error";
        }

    }

    @PostMapping("/section/{id}/edit") 
    public String updateSection(Model model, @PathVariable long id, Section updatedSection) {
        Optional<Section> op = sectionService.findById(id);

        if (op.isPresent()) {
            Section oldSection = op.get();
            sectionService.update(oldSection, updatedSection);
            return "redirect:/section/" + id;
        } else {
            model.addAttribute("message", "No se ha encontrado una sección con ese nombre");
            return "error";
        }

    }

}
