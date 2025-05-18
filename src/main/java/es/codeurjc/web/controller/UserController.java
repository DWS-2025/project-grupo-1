package es.codeurjc.web.controller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.RankingService;
import es.codeurjc.web.service.SectionService;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class UserController {

    @Autowired
    private RankingService rankingService;

    @Autowired
    private UserService userService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping({"/home", "/"})
    /* THIS METHOD WILL BE USED IN THE NEXT PHASE */
    public String index(Model model, HttpServletRequest request) {
        // We add the user name to the model to show it in the home page, if theres any
        // problem with the user name we show "Invitado" as a default value.
        if (request.getUserPrincipal() != null) {
            model.addAttribute("user", userService.getLoggedUser(request.getUserPrincipal().getName()));
            model.addAttribute("session", true);

        } else {
            User user = new User();
            user.setUserName("Invitado");
            model.addAttribute("user", user);
            model.addAttribute("session", false);
        }

        return "home";
    }

    @GetMapping("/following")
    public String following(Model model, HttpServletRequest request) {
        model.addAttribute("sections",
                userService.getLoggedUser(request.getUserPrincipal().getName()).followedSections());
        model.addAttribute("followed", true);
        model.addAttribute("topUsers",
                rankingService.topUsersFollowed(userService.getLoggedUser(request.getUserPrincipal().getName())));
        model.addAttribute("topPosts",
                rankingService.topPostsFollowed(userService.getLoggedUser(request.getUserPrincipal().getName())));

        return "following";
    }

    @GetMapping("/discover")
    public String discover(Model model, HttpServletRequest request) {

        model.addAttribute("sections", sectionService.findNotFollowedSections(request));
        model.addAttribute("topUsers", rankingService.topUsersApp());
        model.addAttribute("topPosts", rankingService.topPostsApp());

        return "discover";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("Error", false);
        return "login";
    }

    @PostMapping("/login")
    public String postMethodName(Model model, @RequestParam String userName, @RequestParam String password) {
        if (userService.getLoggedUser(userName) == null) {
            model.addAttribute("Error", true);
            return "login";
        } else {
            return "redirect:/home";
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("Error", false);
        model.addAttribute("PassError", false);
        return "register";
    }

    @PostMapping("/register")
    public String postMethodName(Model model, @RequestParam String userName, @RequestParam String email,
            @RequestParam String password, @RequestParam String confirmedPassword) {

        for (UserDTO user : userService.findAllUsers()) {
            if (user.email().equals(email) || user.userName().equals(userName)) {
                if (!password.equals(confirmedPassword)) {
                    model.addAttribute("Error", true);
                    model.addAttribute("PassError", true);
                    return "/register";
                } else {
                    model.addAttribute("Error", true);
                    model.addAttribute("PassError", false);
                    return "/register";
                }
            }
        }
        if (!password.equals(confirmedPassword)) {
            model.addAttribute("Error", false);
            model.addAttribute("PassError", true);
            return "/register";
        }
        User newUser = new User(userName, passwordEncoder.encode(password), email, "USER");
        userService.save(newUser);
        return "redirect:/login";
    }

    @GetMapping("/profile/{userId}")
    public String showProfile(Model model, @PathVariable Long userId, HttpServletRequest request) {
        UserDTO user = userService.getUserById(userId);
        UserDTO loggedUser = userService.getLoggedUser(request.getUserPrincipal().getName());
        if (user != null) {
            model.addAttribute("numberOfPublications", user.posts().size());
            model.addAttribute("numberOfFollowers", user.followers().size());
            model.addAttribute("numberOfFollowing", user.followings().size());
            model.addAttribute("numberOfFollowedSections", user.followedSections().size());
            model.addAttribute("user", user);
            model.addAttribute("id", user.id());

            if (!Objects.equals(user.id(), loggedUser.id())) {
                model.addAttribute("notSameUser", true);
            } else {
                model.addAttribute("notSameUser", false);
            }
            if (userService.checkIfTheUserIsFollowed(user, request)) {
                model.addAttribute("followed", true);
            }
            if (userService.checkIsSameUser(userId, request)) {
                model.addAttribute("ShowButtons", true);
            }

            return "profile";

        } else {
            model.addAttribute("message", "No se ha encontrado ese usuario");
            return "error";
        }

    }

    @GetMapping("/editProfile/{userId}")
    public String getMethodName(Model model, @PathVariable long userId, HttpServletRequest request) {
        if (userService.checkIsSameUser(userId, request)) {
            model.addAttribute("User", userService.getUserById(userId));
            return "editProfile";
        } else {
            model.addAttribute("message", "No puedes editar el perfil de otro usuario");
            return "error";
        }

    }

    @PostMapping("/editProfile/{userId}")
    public String processUserEdit(Model model, @PathVariable long userId, @RequestParam String newUserName,
            @RequestParam(required = false) String description, @RequestParam(required = false) MultipartFile userImage, HttpServletRequest request)
            throws IOException, SQLException {

        if (userService.checkIsSameUser(userId, request)) {
            UserDTO user = userService.getUserById(userId);

            if (user == null) {
                model.addAttribute("message", "No se ha encontrado ese usuario");
                return "error";
            }
            userService.updateWebUser(userId, newUserName, description, userImage);

            return "redirect:/profile/" + user.id();
        } else {
            model.addAttribute("message", "No puedes editar el perfil de otro usuario");
            return "error";
        }
    }

    @GetMapping("/user/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        UserDTO userDTO = userService.findById(id);

        if (userDTO != null) {
            Blob image = userService.getImage(id);
            Resource file = new InputStreamResource(image.getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").contentLength(image.length())
                    .body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/deleteUser/{userId}")
    public String postDeleteUser(Model model, @PathVariable long userId, HttpSession loggedU, HttpServletRequest request) {
        if (userService.checkIsSameUser(userId, request)) {

            if (userService.findById(userId) != null) {
                UserDTO userToDelete = userService.getUserById(userId);
                userService.deleteUser(userToDelete);
                model.addAttribute("name", userToDelete.userName());
                model.addAttribute("byPost", true);
                return "user_delete";
            } else {
                model.addAttribute("message", "You are not allowed to do this");
                return "error";
            }
        } else {
            model.addAttribute("message", "You are not allowed to do this");
            return "error";
        }
    }

    @GetMapping("/deleteUser/{userId}")
    public String DeleteUser(Model model, @PathVariable long userId) {
        if (userService.findById(userId) != null) {
            return "user_delete";
        } else {
            model.addAttribute("message", "This user does not exist");
            return "error";
        }

    }

    @GetMapping("/user/{userId}/unfollow")
    public String unfollowUser(Model model, @PathVariable long userId, HttpServletRequest request) {
        UserDTO userToUnfollow = userService.getUserById(userId);
        if (userService.checkIfTheUserIsFollowed(userToUnfollow, request)){
            userService.unfollowUser(userToUnfollow, request);
            return "redirect:/profile/" + userId;
        } else {
            model.addAttribute("message", "no puedes dejar de seguir a un usuario que no sigues");
            return "error";
        }
    }

    @GetMapping("/user/{userId}/follow")
    public String followUser(Model model, @PathVariable long userId, HttpServletRequest request) {
        UserDTO userTofollow = userService.getUserById(userId);
        if (userTofollow != null) {
            userService.followUser(userTofollow, request);
            return "redirect:/profile/" + userId;
        } else {
            model.addAttribute("message", "This user does not exist");
            return "error";
        }
    }

    @GetMapping("/user/{id}/followed")
    public String followedUsers(Model model, @PathVariable long id) {
        UserDTO user = userService.getUserById(id);
        if (user != null) {
            model.addAttribute("followedUsers", user.followings());
            model.addAttribute("message", "seguidos");
            return "view_followers";
        } else {
            model.addAttribute("message", "This user does not exist");
            return "error";
        }
    }

    @GetMapping("/user/{id}/followings")
    public String followingsUsers(Model model, @PathVariable long id) {
        UserDTO user = userService.getUserById(id);
        if (user != null) {
            model.addAttribute("message", "que le siguen");
            model.addAttribute("followedUsers", user.followers());
            return "view_followers";
        } else {
            model.addAttribute("message", "no se ha encontrado ese usuario");
            return "error";
        }
    }

    @PostMapping("/users/{id}/upload-cv")
    public String uploadCv(@PathVariable Long id, @RequestParam("file") MultipartFile file, Model model, HttpServletRequest request) {
        if (userService.checkIsSameUser(id,request)) {
        try {
            userService.uploadCv(id, file);
        } catch (IOException e) {
            model.addAttribute("message", "Error subiendo el CV: " + e.getMessage());
            return "error";
        }
        return "redirect:/profile/" + id;
    }
    else {
        model.addAttribute("message", "No puedes editar el perfil de otro usuario");
        return "error";
    }
}

    @GetMapping("users/{id}/download-cv")
    public ResponseEntity<Resource> downloadCv(@PathVariable Long id) throws IOException {
        return userService.downloadCV(id);

    }

    @PostMapping("users/{id}/delete-cv")
    public String deleteCv(@PathVariable Long id, Model model, HttpServletRequest request) throws IOException {
        if (userService.checkIsSameUser(id,request)) {
            userService.deleteCv(id);
            return "redirect:/profile/" + id;
        } else {
            model.addAttribute("message", "No puedes editar el perfil de otro usuario");
            return "error";
        }
    }

    @GetMapping("users/admin")
    public String adminPanel(Model model, HttpServletRequest request) {
        if (request.isUserInRole("ADMIN")) {
        model.addAttribute("users", userService.getOnlyUsersRole(request));
        return "adminPanel";
        } else {
            model.addAttribute("message", "No se ha encontrado la página solicitada");
            return "error";
        }

    }
}
