package es.codeurjc.web.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.web.Manager;
import es.codeurjc.web.Model.Post;





@Controller
public class PostController {
    @Autowired
    private Manager manager;

    @GetMapping("/post-form")
    public String getMethodName(Model model) {
        return "post-form";
    }    

    @PostMapping("/createPost")
    public String post(Model model, Post post) {
        model.addAttribute("post", post);
        return "view_post";
    }

}