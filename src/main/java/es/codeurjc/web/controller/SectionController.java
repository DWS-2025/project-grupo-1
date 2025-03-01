package es.codeurjc.web.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import es.codeurjc.web.model.Section;
import es.codeurjc.web.service.SectionService;
import es.codeurjc.web.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class SectionController {
    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;

    private static String IMAGE_FOLDER = "src/main/resources/static/images/";

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

    @PostMapping("/section/new")
    public String createSection(@RequestParam String title, @RequestParam String description,
            @RequestParam MultipartFile sectionImage) {

        try {
            Files.createDirectories(Paths.get(IMAGE_FOLDER)); // Create the directory if it doesn't exist

            String imageName = UUID.randomUUID().toString() + "_" + sectionImage.getOriginalFilename();
            Path filePath = Paths.get(IMAGE_FOLDER + imageName); 

            Files.write(filePath, sectionImage.getBytes()); // Save the image in the folder
            Section section = new Section(title, description, imageName);
            sectionService.saveSection(section);

        } catch (IOException e) {
            e.printStackTrace();
        }

       

        return "redirect:/section";
    }

    @PostMapping("/section/{id}/delete")
    public String deleteSection(Model model, @PathVariable long id) {
        es.codeurjc.web.model.Section section = sectionService.findById(id).get();
        sectionService.deleteSection(section);

        return "delete_section";
    }

    @GetMapping("/section/{id}")
    public String viewSection(Model model, @PathVariable long id) {
        Optional<Section> section = sectionService.findById(id);

        if (section.isPresent()) {
            model.addAttribute("posts", section.get().getPosts());
            model.addAttribute("section", section.get());
        }

        return "view_section";
    }

}
