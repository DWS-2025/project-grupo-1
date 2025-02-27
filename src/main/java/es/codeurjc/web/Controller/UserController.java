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
import es.codeurjc.web.service.Manager;
import es.codeurjc.web.service.UserService;
import es.codeurjc.web.RankingManager;

@Controller
public class UserController {
    @Autowired
    // This is the manager that contains all the information of the application.
    // With @Autowired we are telling Spring to inject the manager here, and it
    // creates only one instance of the manager.
    private Manager manager;
    private User user;

    @Autowired
    private UserService userService;


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
        model.addAttribute("Sections", manager.getMainUser().getFollowedSections());
        model.addAttribute("topUsers", rankingManager.topUsersFollowed(manager.getMainUser()));
        model.addAttribute("topPosts", rankingManager.topPostsFollowed(manager.getMainUser()));

        return "following";
    }

    @GetMapping("/discover")
    public String discover(Model model) {
        model.addAttribute("Sections", manager.getSections());
        model.addAttribute("topUsers", rankingManager.topUsersApp());
        model.addAttribute("topPosts", rankingManager.topPostsApp());

        return "discover";
    }

    @GetMapping({ "/login" })
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/profile/{userName}")
    public String showProfile(Model model, @PathVariable String userName) {
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
            model.addAttribute("userName", manager.getMainUser().getName());
            model.addAttribute("numberOfPublications", manager.getMainUser().getPosts().size());
            model.addAttribute("numberOfFollowers", manager.getMainUser().getFollowers().size());
            model.addAttribute("numberOfFollowings", manager.getMainUser().getFollowing().size());
            model.addAttribute("numberOfFollowedSections", manager.getMainUser().getFollowedSections().size());
            model.addAttribute("userDescription", manager.getMainUser().getDescription());
            model.addAttribute("Post", manager.getMainUser().getPosts());
            model.addAttribute("rate", manager.getMainUser().getUserRate());
            return "profile";

        }

    }

    @GetMapping("/editarPerfil/{userName}")
    public String getMethodName(Model model, @PathVariable String userName) {
        model.addAttribute("User", manager.getUser(userName));
        return "editProfile";
    }

    @PostMapping("/editarPerfil/{userName}")
    public String processUserEdit(Model model, @PathVariable String userName, @RequestParam String newUserName,
            @RequestParam String description, @RequestParam(required = false) MultipartFile userImage) {
        User user = manager.getUser(userName);
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
