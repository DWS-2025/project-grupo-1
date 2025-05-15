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
        // Obtén el usuario autenticado desde el SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            // El principal debe ser una instancia de User (Spring Security)
            User principal = (User) authentication.getPrincipal();
            String username = principal.getUsername();

            // Obtén el usuario autenticado desde el servicio
            UserDTO user = userService.findByUserNameAuth(username);
            model.addAttribute("loggedUser", user);
        } else {
            // Si no hay usuario autenticado, añade un valor nulo o vacío
            model.addAttribute("loggedUser", null);
        }
    }
}