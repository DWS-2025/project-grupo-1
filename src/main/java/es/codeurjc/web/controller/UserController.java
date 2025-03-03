package es.codeurjc.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.User;
import es.codeurjc.web.service.RankingService;
import es.codeurjc.web.service.SectionService;
import es.codeurjc.web.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class UserController {

    @Autowired
    private RankingService rankingService;

    @Autowired
    private UserService userService;

    @Autowired
    private SectionService sectionService;


    @GetMapping({ "/home", "/" })
    public String index(Model model) {
        // We add the user name to the model to show it in the home page, if theres any
        // problem with the user name we show "Invitado" as a default value.
        if (userService.getLoggedUser() != null) {
            model.addAttribute("user", userService.getLoggedUser());
        } else {
            User user = new User();
            user.setName("Invitado");
            model.addAttribute("user", user);
        }

        return "home";
    }

    @GetMapping("/following")
    public String following(Model model) {
        model.addAttribute("sections", userService.getLoggedUser().getFollowedSections());
        model.addAttribute("topUsers", rankingService.topUsersFollowed(userService.getLoggedUser()));
        model.addAttribute("topPosts", rankingService.topPostsFollowed(userService.getLoggedUser()));

        return "following";
    }

    @GetMapping("/discover")
    public String discover(Model model) {
        model.addAttribute("sections", sectionService.findAll());
        model.addAttribute("topUsers", rankingService.topUsersApp());
        model.addAttribute("topPosts", rankingService.topPostsApp());

        return "discover";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/profile/{userId}")
    public String showProfile(Model model, @PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            model.addAttribute("userId", user.getId());
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImage", user.getUserImage());
            if (user.getDescription() == null) {
                model.addAttribute("userDescription", "No description");
                
            }else{
                model.addAttribute("userDescription", user.getDescription());
            }
            model.addAttribute("numberOfPublications", user.getPosts().size());
            model.addAttribute("numberOfFollowers", user.getFollowers().size());
            model.addAttribute("numberOfFollowing", user.getFollowing().size());
            model.addAttribute("numberOfFollowedSections", user.getFollowedSections().size());
            model.addAttribute("rate", user.getUserRate());
            model.addAttribute("posts",user.getPosts());
            return "profile";
        } else {
            return "login";
        }
    }

    @GetMapping("/editProfile/{userId}")
    public String getMethodName(Model model, @PathVariable long userId) {
        model.addAttribute("User", userService.getUserById(userId));
        return "editProfile";
    }

    @PostMapping("/editProfile/{userId}")
    public String processUserEdit(Model model, @PathVariable long userId, @RequestParam String newUserName,
            @RequestParam(required = false) String description, @RequestParam(required = false) MultipartFile userImage) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return "error";
        }

        if (newUserName != null && !newUserName.isEmpty()) {
            user.setName(newUserName);
        }

        if (description != null && !description.isEmpty()) {
            user.setDescription(description);
        }
        return "redirect:/profile/" + user.getId();
    }
    

    @GetMapping("/deleteUser/{userId}")
    public String postMethodName(Model model, @PathVariable long userId) {
        User deletedUser = userService.getUserById(userId);
        userService.deleteUser(deletedUser);
        model.addAttribute("name", deletedUser.getName());
        return "user_delete";
    }
    
}