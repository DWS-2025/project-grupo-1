package es.codeurjc.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import es.codeurjc.web.model.User;
import es.codeurjc.web.service.PostService;
import es.codeurjc.web.service.UserService;

@Controller
public class MainController {

    @Autowired
    // This is the manager that contains all the information of the application.
    // With @Autowired we are telling Spring to inject the manager here, and it
    // creates only one instance of the manager.
    private UserService userService;
    private PostService postService;

    @PostMapping("/procesarFormulario")
    public String postMethodName(@RequestBody String userName, @RequestBody String password,
            @RequestBody String email) {
        User user = new User(userName, password, email);
        userService.save(user);
        return "redirect:/profile";
    }

}
