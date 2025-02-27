package es.codeurjc.web.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.Model.User;
import es.codeurjc.web.service.SectionService;
import es.codeurjc.web.service.UserService;

@Controller
public class UserController {
    // This is the manager that contains all the information of the application.
    // With @Autowired we are telling Spring to inject the manager here, and it
    // creates only one instance of the manager.
/*   @Autowired
    private RankingManager ranking;*/

    @Autowired
    private UserService userService;

    @Autowired
    private SectionService sectionService;


    @GetMapping({ "/home", "/" })
    public String index(Model model) {
        // We add the user name to the model to show it in the home page, if theres any
        // problem with the user name we show "Invitado" as a default value.
        if (userService.getLoggedUser() != null) {
            model.addAttribute("userName", userService.getLoggedUser().getName());
        } else {
            model.addAttribute("userName", "Invitado");
        }

        return "home";
    }

    @GetMapping("/following")
    public String following(Model model) {
        model.addAttribute("Sections", userService.getLoggedUser().getFollowedSections());
//        model.addAttribute("topUsers", ranking.topUsersFollowed(userService.getLoggedUser()));
//        model.addAttribute("topPosts", ranking.topPostsFollowed(userService.getLoggedUser()));

        return "following";
    }

    @GetMapping("/discover")
    public String discover(Model model) {
        model.addAttribute("Sections", sectionService.findAll());
//        model.addAttribute("topUsers", ranking.topUsersApp());
//        model.addAttribute("topPosts", ranking.topPostsApp());

        return "discover";
    }

    @GetMapping({ "/login" })
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/profile/{id}")
    public String showProfile(Model model, @PathVariable Long id) {
        User user = userService.getUserById(id);
        // We check if the user is logged in, if it is we show the user information, if
        // not we show the main user information.
        if (user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImage", user.getUserImage());
            model.addAttribute("userDescription", user.getDescription());
            model.addAttribute("numberOfPublications", user.getPosts().size());
            model.addAttribute("numberOfFollowers", user.getFollowers().size());
            model.addAttribute("numberOfFollowing", user.getFollowing().size());
            model.addAttribute("numberOfFollowedSections", user.getFollowedSections().size());
            model.addAttribute("rate", user.getUserRate());
            return "profile";
        } else {
            return "login";

        }

    }

    @GetMapping("/editarPerfil/{userName}")
    public String getMethodName(Model model, @PathVariable long id) {
        model.addAttribute("User", userService.getUserById(id));
        return "editProfile";
    }

    @PostMapping("/editarPerfil/{id}")
    public String processUserEdit(Model model, @PathVariable long id, @RequestParam String newUserName,
            @RequestParam String description, @RequestParam(required = false) MultipartFile userImage) {
        User user = userService.getUserById(id);
        if (user == null) {
            // Manejar el caso en que el usuario no esté inicializado
            return "error";
        }

        if (newUserName != null && !newUserName.isEmpty()) {
            user.setName(newUserName);
        }

        if (description != null && !description.isEmpty()) {
            user.setDescription(description);
        }
        return "redirect:/profile/" + user.getName();
    }
}