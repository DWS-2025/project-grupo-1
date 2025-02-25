package es.codeurjc.web.Controller;

import es.codeurjc.web.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;




@Controller
public class PostController {
    @Autowired
    private Manager manager;

    @PostMapping("/createPost")
    public String post(Model model, @RequestParam String title, @RequestParam String content) {
        model.addAttribute("title", title);
        model.addAttribute("content", content);
        return "home";
    }

}