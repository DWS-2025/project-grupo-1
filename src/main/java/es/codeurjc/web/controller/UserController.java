package es.codeurjc.web.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.Section;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.ImageUserService;
import es.codeurjc.web.service.RankingService;
import es.codeurjc.web.service.SectionService;
import es.codeurjc.web.service.UserService;


@Controller
public class UserController {

    @Autowired
    private RankingService rankingService;

    @Autowired
    private UserService userService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ImageUserService imageUserService;

    private static final String USERS_FOLDER = "users";

    @GetMapping({ "/home", "/" })
    public String index(Model model) {
        // We add the user name to the model to show it in the home page, if theres any
        // problem with the user name we show "Invitado" as a default value.
        if (userService.getLoggedUser() != null) {
            model.addAttribute("user", userService.getLoggedUser());
        } else {
            User user = new User();
            user.setName("Invitado");
            model.addAttribute("user", user);
        }

        return "home";
    }

    @GetMapping("/following")
    public String following(Model model) {
        model.addAttribute("sections", userService.getLoggedUser().getFollowedSections());
        model.addAttribute("followed", true);
        model.addAttribute("topUsers", rankingService.topUsersFollowed(userService.getLoggedUser()));
        model.addAttribute("topPosts", rankingService.topPostsFollowed(userService.getLoggedUser()));

        return "following";
    }

    @GetMapping("/discover")
    public String discover(Model model) {
        List<Section> allSections = sectionService.findAll();
        List<Section> followedSections = userService.getLoggedUser().getFollowedSections();
        // Filter only the sections that are NOT in the list of followed sections
        List<Section> notFollowedSections = allSections.stream()
                .filter(section -> !followedSections.contains(section))
                .collect(Collectors.toList());
        model.addAttribute("sections", notFollowedSections);
        model.addAttribute("topUsers", rankingService.topUsersApp());
        model.addAttribute("topPosts", rankingService.topPostsApp());

        return "discover";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String login(Model model, @RequestParam String userName, @RequestParam String password ) {
        User logingUser = userService.findByUserName(userName);
        if(logingUser == null || !logingUser.getPassword().equals(password)){
            model.addAttribute("Error", "usuario o contraseña no válidos");
            return "redirect:/login";
        }
        return "redirect:/";
    }

    @GetMapping("/register")
    public String register(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String postMethodName(Model model, @RequestParam String userName,@RequestParam String email,
    @RequestParam String password, @RequestParam String confirmedPassword) {
        List<User> users = userService.findAllUsers();
        for (User user : users) {
            if(user.getEmail().equals(email) || user.getName().equals(userName)){
                model.addAttribute("Error", "Usuario existente o correo utilizado");
                return "redirect:/register";
            }
        }
        if(!password.equals(confirmedPassword)){
            model.addAttribute("PassError", "Las contraseñas no coinciden");
            return "redirect:/register";
        }
        User newUser = new User(userName, password,email);
        userService.save(newUser);
        return "redirect:/";
    }


    @GetMapping("/profile/{userId}")
    public String showProfile(Model model, @PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            model.addAttribute("numberOfPublications", user.getPosts().size());
            model.addAttribute("numberOfFollowers", user.getFollowers().size());
            model.addAttribute("numberOfFollowing", user.getFollowings().size());
            model.addAttribute("numberOfFollowedSections", user.getFollowedSections().size());
            model.addAttribute("user", user);
        
            if (user != userService.getLoggedUser()) {
            model.addAttribute("notSameUser", true);
            }
            if (userService.getLoggedUser().getFollowings().contains(user)) {
            model.addAttribute("followed", true);
            }
            return "profile";

        } else {
            model.addAttribute("message", "No se ha encontrado ese usuario");
            return "error";
        }

    }

    @GetMapping("/editProfile/{userId}")
    public String getMethodName(Model model, @PathVariable long userId) {
        model.addAttribute("User", userService.getUserById(userId));
        return "editProfile";
    }

    @PostMapping("/editProfile/{userId}")
    public String processUserEdit(Model model, @PathVariable long userId, @RequestParam String newUserName,
            @RequestParam(required = false) String description, @RequestParam(required = false) MultipartFile userImage)
            throws IOException {

        User user = userService.getUserById(userId);

        if (user == null) {
            model.addAttribute("message", "No se ha encontrado ese usuario");
            return "error";
        }

        if (newUserName != null && !newUserName.isEmpty()) {
            user.setName(newUserName);
        }

        if (description != null && !description.isEmpty()) {
            user.setDescription(description);
        }

        if (userImage != null && !userImage.isEmpty()) {
            imageUserService.saveImage(USERS_FOLDER, user.getId(), userImage);
            String imageName = userImage.getOriginalFilename();
            user.setUserImage(imageName);
        }
        userService.save(user);

        return "redirect:/profile/" + user.getId();
    }

    @GetMapping("/user/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws MalformedURLException {
        return imageUserService.createResponseFromImage(USERS_FOLDER, id);
    }

    @GetMapping("/deleteUser/{userId}")
    public String postMethodName(Model model, @PathVariable long userId) {
        User deletedUser = userService.getUserById(userId);
        userService.deleteUser(deletedUser);
        model.addAttribute("name", deletedUser.getName());
        return "user_delete";
    }

    @GetMapping("/user/{userId}/unfollow")
    public String unfollowUser(Model model, @PathVariable long userId) {
        User userToUnfollow = userService.getUserById(userId);
        userService.getLoggedUser().unfollow(userToUnfollow);
        return "redirect:/profile/" + userId;
    }

    @GetMapping("/user/{userId}/follow")
    public String followUser(Model model, @PathVariable long userId) {
        User userToUnfollow = userService.getUserById(userId);
        userService.getLoggedUser().follow(userToUnfollow);
        return "redirect:/profile/" + userId;
    }
    @GetMapping("/user/{id}/followed")
    public String followedUsers(Model model, @PathVariable long id) {
        User user = userService.getUserById(id);
        model.addAttribute("followedUsers", user.getFollowings());
        model.addAttribute("message", "seguidos");
        return "view_followers";
    }
    @GetMapping("/user/{id}/followings")
    public String followingsUsers(Model model, @PathVariable long id) {
        User user = userService.getUserById(id);
        model.addAttribute("message", "que le siguen");
        model.addAttribute("followedUsers", user.getFollowers());
        return "view_followers";
    }
}
