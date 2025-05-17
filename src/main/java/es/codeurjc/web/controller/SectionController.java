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

@Controller
public class SectionController {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;

    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if(principal != null) {
		
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

    /* @PostMapping("/section/new")
    public String createSection(@RequestParam String title, @RequestParam String description,
            @RequestParam MultipartFile sectionImage) {
        try {
            Section section = new Section(title, description);
            SectionDTO sectionDTO = sectionService.toDto(section);
            sectionService.saveSectionWithImageSection(sectionDTO, sectionImage);
            return "redirect:/section";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    } */

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


    /*
     * @GetMapping("/section/{id}/image")
     * public ResponseEntity<Object> downloadImage(@PathVariable long id) throws
     * MalformedURLException {
     * return imageSectionService.createResponseFromImage(SECTIONS_FOLDER, id);
     * }
     */

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
    public String followSection(Model model, @PathVariable long id,HttpServletRequest request) {
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
    public String updateSection(RedirectAttributes redirectAttributes, Model model, @PathVariable long id, SectionDTO updatedSection, MultipartFile newImage) throws IOException {
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

   /* @GetMapping("/section/search")
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
    */

    @GetMapping("/section/search")
    public String searchSection(Model model, @RequestParam(required = false) List<String> filters){
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
            } 
            else if (filters.contains("minRating") && filters.contains("minPosts") && !filters.contains("title")) {
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
