package es.codeurjc.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContollerTest {

    @Autowired
    // This is the manager that contains all the information of the application. With @Autowired we are telling Spring to inject the manager here, and it creates only one instance of the manager.
    private Manager manager;

    @GetMapping({"/home", "/"})
    public String index(Model model) {
        // We add the user name to the model to show it in the home page, if theres any problem with the user name we show "Invitado" as a default value.
        if (manager.getMainUser() != null) {
            model.addAttribute("userName", manager.getMainUser().getName());
        } else {
            model.addAttribute("userName", "Invitado");
        }

        return "home";
    }

    @GetMapping("/following")
    public String following(Model model) {
        return "following";
    }

    @GetMapping("/post")
    public String post(Model model) {
        return "post";
    }

    @GetMapping("/discover")
    public String discover(Model model) {
        return "discover";
    }

    @GetMapping("/profile")
    public String getMethodName(Model model) {
        model.addAttribute("userName", manager.getMainUser().getName());
        model.addAttribute("NumberOfPublications", manager.getMainUser().getPosts().size());
        model.addAttribute("NumberOfFollowers", manager.getMainUser().getFollowers().size());
        model.addAttribute("NumberOfFollowings", manager.getMainUser().getFollowing().size());
        model.addAttribute("NumberOfFollowingSections", manager.getMainUser().getFollowedSections().size());
        model.addAttribute("userDescription", manager.getMainUser().getDescription());
        model.addAttribute("Post", manager.getMainUser().getPosts());
        return "profile";
    }

    @GetMapping({"/login"})
    public String login(Model model) {
        return "login";
    }
}
