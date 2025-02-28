package es.codeurjc.web.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import es.codeurjc.web.Model.Section;
import es.codeurjc.web.service.SectionService;
import es.codeurjc.web.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class SectionController {
    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;

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
    public String createSection(Section section) {
        sectionService.saveSection(section);

        return "redirect:/section";
    }

    @PostMapping("/section/{id}/delete")
    public String deleteSection(Model model, @PathVariable long id) {
        Section section = sectionService.findById(id);
        sectionService.deleteSection(section);

        return "redirect:/section";
    }

   
    

}
