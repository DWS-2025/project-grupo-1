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
    @GetMapping("/browse")
    public String browse(Model model) {
        return "browse";
    }
    @GetMapping("/details")
    public String details(Model model) {
        return "details";
    }
    @GetMapping("/streams")
    public String streams(Model model) {
        return "streams";
    }
    @GetMapping("/profile")
    public String getMethodName(Model model) {
        return "profile";
    }
}
