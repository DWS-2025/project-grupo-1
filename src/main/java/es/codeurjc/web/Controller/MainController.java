package es.codeurjc.web.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import es.codeurjc.web.Model.Post;
import es.codeurjc.web.Model.User;
import es.codeurjc.web.service.Manager;
//import es.codeurjc.web.RankingManager;

@Controller
public class MainController {

    @Autowired
    // This is the manager that contains all the information of the application.
    // With @Autowired we are telling Spring to inject the manager here, and it
    // creates only one instance of the manager.
    private Manager manager;
    private User user;
    


    @PostMapping("/procesarFormulario")
    public String postMethodName(@RequestBody String userName, @RequestBody String password,
            @RequestBody String email) {

        user.setName(userName);
        user.setPassword(password);
        user.setEmail(email);

        return "redirect:/profile";
    }

    @GetMapping("/view_post/{postTitle}")
    public String showUserPost(Model model, @PathVariable String postTitle) {
        Post requestedPost = new Post();
        for (Post post : manager.getAplicationPosts()) {
            if (post.getTitle().equals(postTitle)) {
                model.addAttribute("post", post);
                requestedPost = post;
            }
        }
        // We check if the post exists, if it doesn't we show an error page explaining
        // the problem.
        if (requestedPost.getTitle() == null) {
            model.addAttribute("errorType", "No se ha encontrado ningun post con el titulo :" + postTitle);
            return "error";
        } else {
            model.addAttribute("Post", requestedPost);
            return "view_post";
        }
    }
}
