package es.codeurjc.web.restController;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ControllerAdvice;
import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.service.UserService;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;

    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addUserToModel(Model model) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
          // Check if the principal is an instance of User
            User principal = (User) authentication.getPrincipal();
            String username = principal.getUsername();

           // Fetch the user details using the username
            UserDTO user = userService.findByUserNameAuth(username);
            model.addAttribute("loggedUser", user);
        } else {
            // User is not authenticated or principal is not a User object
            model.addAttribute("loggedUser", null);
        }
    }
}