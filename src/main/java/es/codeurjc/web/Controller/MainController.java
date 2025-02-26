package es.codeurjc.web.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.Manager;
import es.codeurjc.web.Model.Comment;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.Model.User;
import es.codeurjc.web.RankingManager;

@Controller
public class MainController {

    @Autowired
    // This is the manager that contains all the information of the application.
    // With @Autowired we are telling Spring to inject the manager here, and it
    // creates only one instance of the manager.
    private Manager manager;
    private User user;
    @Autowired
    private RankingManager rankingManager;

    @GetMapping({"/home", "/"})
    public String index(Model model) {
        // We add the user name to the model to show it in the home page, if theres any
        // problem with the user name we show "Invitado" as a default value.
        if (manager.getMainUser() != null) {
            model.addAttribute("userName", manager.getMainUser().getName());
        } else {
            model.addAttribute("userName", "Invitado");
        }

        return "home";
    }

    // @GetMapping("/post")
    // public String post(Model model) {
    //     return "post";
    // }

    @GetMapping("/following")
    public String following(Model model) {
        model.addAttribute("Sections", manager.getMainUser().getFollowedSections());
        model.addAttribute("topUsers", rankingManager.topUsersFollowed(manager.getMainUser()));
        model.addAttribute("topPosts", rankingManager.topPostsFollowed(manager.getMainUser()));

        return "following";
    }

    @GetMapping("/discover")
    public String discover(Model model) {
        model.addAttribute("Sections", manager.getSections());
        model.addAttribute("topUsers", rankingManager.topUsersApp());
        model.addAttribute("topPosts", rankingManager.topPostsApp());

        return "discover";
    }

    @GetMapping({"/login"})
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/profile/{userName}")
    public String showProfile(Model model, @PathVariable String userName) {
        // We check if the user is logged in, if it is we show the user information, if
        // not we show the main user information.
        if (user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImage", user.getUserImage());
            model.addAttribute("userDescription", user.getDescription());
            model.addAttribute("numberOfPublications", user.getPosts().size());
            model.addAttribute("numberOfFollowers", user.getFollowers().size());
            model.addAttribute("numberOfFollowing", user.getFollowing().size());
            model.addAttribute("numberOfFollowedSections", user.getFollowedSections().size());
            model.addAttribute("rate", user.getUserRate());
            return "profile";
        } else {
            model.addAttribute("userName", manager.getMainUser().getName());
            model.addAttribute("numberOfPublications", manager.getMainUser().getPosts().size());
            model.addAttribute("numberOfFollowers", manager.getMainUser().getFollowers().size());
            model.addAttribute("numberOfFollowings", manager.getMainUser().getFollowing().size());
            model.addAttribute("numberOfFollowedSections", manager.getMainUser().getFollowedSections().size());
            model.addAttribute("userDescription", manager.getMainUser().getDescription());
            model.addAttribute("Post", manager.getMainUser().getPosts());
            model.addAttribute("rate", manager.getMainUser().getUserRate());
            return "profile";

        }

    }

    @PostMapping("/procesarFormulario")
    public String postMethodName(@RequestBody String userName, @RequestBody String password,
            @RequestBody String email) {

        user.setName(userName);
        user.setPassword(password);
        user.setEmail(email);

        return "redirect:/profile";
    }

    @GetMapping("/editarPerfil/{userName}")
    public String getMethodName(Model model, @PathVariable String userName) {
        model.addAttribute("User", manager.getUser(userName));
        return "editProfile";
    }

    @PostMapping("/editarPerfil/{userName}")
    public String processUserEdit(Model model, @PathVariable String userName, @RequestParam String newUserName, @RequestParam String description, @RequestParam(required = false) MultipartFile userImage) {
        User user = manager.getUser(userName);
        if (user == null) {
            // Manejar el caso en que el usuario no esté inicializado
            return "error";
        }

        if (newUserName != null && !newUserName.isEmpty()) {
            user.setName(newUserName);
        }

        if (description != null && !description.isEmpty()) {
            user.setDescription(description);
        }
        return "redirect:/profile/" + user.getName();
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

    @GetMapping("/comment_form/{postTitle}")
    public String commentForm(Model model, @PathVariable String postTitle) {
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
            model.addAttribute("isEditing", false);
            model.addAttribute("Post", requestedPost);
            return "/comment_form";
        }

    }

    @PostMapping("/comment_form/{postTitle}")
    public String sendComment(Model model, @RequestParam String content, @RequestParam int rating,
            @PathVariable String postTitle) {
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
        } else if (rating < 0 || rating > 5 ) {
            model.addAttribute("errorType", "La valoración debe estar entre 0 y 5");
            return "error";
        } else if (content == null || content.isEmpty()) {
            model.addAttribute("errorType", "El comentario no puede estar vacio");
            return "error";
        } else {
            Comment newComment = new Comment(content, manager.getMainUser(), rating);
            requestedPost.addComment(newComment);
            return "redirect:/view_post/" + postTitle;
        }

    }

    @GetMapping("/edit_comment/{postTitle}/{commentId}")
    public String editCommentForm(Model model, @PathVariable String postTitle, @PathVariable int commentId) {
        Post requestedPost = new Post();
        Comment requestedComment = new Comment();

        // Look for the post and the comment
        for (Post post : manager.getAplicationPosts()) {
            if (post.getTitle().equals(postTitle)) {
                requestedPost = post;
                // We get the comment with the id commentId, -1 because the id starts at 1 {{-index}})
                break;
            }
        }

        if (requestedPost.getTitle() == null) {
            model.addAttribute("errorType", "No se ha encontrado ningún post con el título: " + postTitle);
            return "error";
        } 
           
        model.addAttribute("Post", requestedPost);
        model.addAttribute("isEditing", true);
        model.addAttribute("commentId", commentId);
        model.addAttribute("content", requestedPost.getComment(commentId).getCommentContent());
        return "comment_form";
    }

    @PostMapping("/edit_comment/{postTitle}/{commentId}")
    public String updateComment(Model model, @PathVariable String postTitle, @PathVariable int commentId, @RequestParam String content, @RequestParam int rating) {
        Post requestedPost = new Post();
        for (Post post : manager.getAplicationPosts()) {
            if (post.getTitle().equals(postTitle)) {
                requestedPost = post;     
                break;
            }
        }
        if (rating < 0 || rating > 5) {
            model.addAttribute("errorType", "La valoración debe estar entre 0 y 5");
            return "error";
        } else if (content == null || content.isEmpty()) {
            model.addAttribute("errorType", "El comentario no puede estar vacio");
            return "error";
        } else {
            requestedPost.getComment(commentId - 1).updateComment(content, rating);
            model.addAttribute("Post", requestedPost);
            return "redirect:/view_post" + postTitle;
        }
    }
}
