package es.codeurjc.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class SessionController {

    @Autowired
    private User user;

    @PostMapping("/procesarFormulario")
    public String postMethodName(@RequestBody String userName, @RequestBody String password, @RequestBody String email) {
        
        user.setUserImage(userName);
        user.setPassword(password);
        user.setEmail(email);
        
        return "redirect:/profile";
    }
    

    @GetMapping("/profile")
    public String showProfile(Model model) {
        model.addAttribute("userName", user.getName());
        model.addAttribute("userImage", user.getUserImage());
        model.addAttribute("userDescription", user.getDescription());
        model.addAttribute("numberOfPublications", user.getPosts().size());
        model.addAttribute("numberOfFollowers", user.getFollowers().size());
        model.addAttribute("numberOfFollowing", user.getFollowing().size());
        model.addAttribute("numberOfFollowedSections", user.getFollowedSections().size());
        model.addAttribute("rate", user.getRate());
        return "profile";
    }

}