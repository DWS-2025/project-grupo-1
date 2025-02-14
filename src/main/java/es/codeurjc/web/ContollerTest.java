package es.codeurjc.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class ContollerTest {
    @GetMapping({"/home", "/"})
    public String index(Model model) {
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
        return "profile";
    }
}
