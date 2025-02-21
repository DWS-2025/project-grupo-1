package es.codeurjc.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @Autowired
    // This is the manager that contains all the information of the application.
    // With @Autowired we are telling Spring to inject the manager here, and it
    // creates only one instance of the manager.
    private Manager manager;
    private User user;
    @Autowired
    private RankingManager rankingManager;

    @GetMapping({"/home", "/"})
    public String index(Model model) {
        // We add the user name to the model to show it in the home page, if theres any
        // problem with the user name we show "Invitado" as a default value.
        if (manager.getMainUser() != null) {
            model.addAttribute("userName", manager.getMainUser().getName());
        } else {
            model.addAttribute("userName", "Invitado");
        }

        return "home";
    }

    @GetMapping("/following")
    public String following(Model model) {
        model.addAttribute("Sections", manager.getMainUser().getFollowedSections());
        return "following";
    }

    @GetMapping("/post")
    public String post(Model model) {
        return "post";
    }

    @GetMapping("/discover")
    public String discover(Model model) {
        model.addAttribute("Sections", manager.getSections());
        model.addAttribute("TopUsers", rankingManager.topUsers());
        model.addAttribute("TopPosts", rankingManager.topPosts());
        return "discover";
    }

    @GetMapping({"/login"})
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
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
            model.addAttribute("rate", user.getRate());
            return "profile";
        } else {
            model.addAttribute("userName", manager.getMainUser().getName());
            model.addAttribute("numberOfPublications", manager.getMainUser().getPosts().size());
            model.addAttribute("numberOfFollowers", manager.getMainUser().getFollowers().size());
            model.addAttribute("numberOfFollowings", manager.getMainUser().getFollowing().size());
            model.addAttribute("numberOfFollowedSections", manager.getMainUser().getFollowedSections().size());
            model.addAttribute("userDescription", manager.getMainUser().getDescription());
            model.addAttribute("Post", manager.getMainUser().getPosts());
            model.addAttribute("rate", manager.getMainUser().getRate());
            return "profile";

        }

    }

    @PostMapping("/procesarFormulario")
    public String postMethodName(@RequestBody String userName, @RequestBody String password,
            @RequestBody String email) {

        user.setName(userName);
        user.setPassword(password);
        user.setEmail(email);

        return "redirect:/profile";
    }

    @GetMapping("/editarPerfil")
    public String getMethodName(Model model) {
        model.addAttribute("User", manager.getMainUser());
        return "editProfile";
    }
    
    @PostMapping("/editarPerfil")
    public String processUserEdit(Model model, @RequestParam String userName, @RequestParam String description, @RequestParam String userImage) {
        user.setName(userName);
        user.setDescription(description);
        if(userImage != null){
        user.setUserImage(userImage);
        }
        return showProfile(model);
    }

    @GetMapping("/view_post")
    public String showUserPost(Model model, @RequestParam String postTitle) {
        Post requestedPost = new Post();
        for (Post post : manager.getAplicationPosts()) {
            if (post.getTitle().equals(postTitle)) {
                model.addAttribute("post", post);
                requestedPost = post;
            }
        }
        if (requestedPost == null) {

            return "error";
        } else {
            model.addAttribute("Post", requestedPost);
            return "view_post";

        }

    }

    @GetMapping("comment_form")
    public String comment(Model model, @RequestParam String postTitle) {
        Post requestedPost = new Post();
        for (Post post : manager.getAplicationPosts()) {
            if (post.getTitle().equals(postTitle)) {
                model.addAttribute("post", post);
                requestedPost = post;
            }
        }
        if (requestedPost == null) {

            return "error";


        } else {
            model.addAttribute("Post", requestedPost);
            return "comment_form";

        }

    }

}
