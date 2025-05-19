package es.codeurjc.web.restController;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.service.UserService;

/**
 * GlobalControllerAdvice is a Spring {@link ControllerAdvice} class that adds common attributes
 * to the model for all controllers. It injects the currently authenticated user's details
 * into the model under the attribute "loggedUser", and if the user is an admin (username "Admin"),
 * it also adds an "admin" attribute.
 *
 * <p>
 * This allows views to access the logged-in user's information and admin status easily.
 * </p>
 *
 * <p>
 * Dependencies:
 * <ul>
 *   <li>{@link UserService} for fetching user details.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Model Attributes:
 * <ul>
 *   <li><b>loggedUser</b>: The authenticated user's details as a {@link UserDTO}, or {@code null} if not authenticated.</li>
 *   <li><b>admin</b>: {@code true} if the authenticated user is "Admin", otherwise not present.</li>
 * </ul>
 * </p>
 * 
 * @author Grupo 1
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;

    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addUserToModel(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            // Check if the principal is an instance of User
            User principal = (User) authentication.getPrincipal();
            String username = principal.getUsername();

            if (username.equals("Admin")) {
                model.addAttribute("admin", username.equals("Admin"));
                UserDTO user = userService.findByUserNameAuth(username);
                model.addAttribute("loggedUser", user);
            }

            // Fetch the user details using the username
            UserDTO user = userService.findByUserNameAuth(username);
            model.addAttribute("loggedUser", user);
        } else {
            // User is not authenticated or principal is not a User object
            model.addAttribute("loggedUser", null);
        }
    }
}