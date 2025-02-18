package es.codeurjc.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Manager {

    private User mainUser;
    private List<User> aplicationUsers;
    private List<Section> sections;

    public Manager() {
        aplicationUsers = new ArrayList<>(); 
        sections = new ArrayList<>();
    }

    public User getMainUser() {
        return mainUser;
    }

    public void setMainUser(User mainUser) {
        this.mainUser = mainUser;
    }

    public User getUser(String userName) {
        for (User user : this.aplicationUsers) {
            if (user.getName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void deleteSection(Section section) {
        sections.remove(section);
    }

    public void addUser(User user) {
        aplicationUsers.add(user);
    }

    public void deleteUser(User user) {
        aplicationUsers.remove(user);
    }

    public void init() {
        // Creates a list of users and add them to the application
        this.aplicationUsers = createUsers();
        // Creates a list of posts and add them to the users(all users have the same posts)
        this.createPosts();
        this.sections = this.createSections();

    }

    public static List<User> createUsers() {
        String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam nec dictum ex. Sed eu lectus ut velit pharetra dictum quis et nisi. Suspendisse in nisl quam. Vestibulum non dapibus magna. Duis volutpat magna eget venenatis egestas. Fusce tincidunt, neque et finibus suscipit, mi tortor facilisis arcu, eu euismod diam magna non metus. Aliquam blandit sodales dui, sit amet imperdiet ipsum euismod sed. Mauris luctus neque eu nulla posuere, sit amet dignissim augue lacinia. Aliquam viverra ullamcorper lacus, sit amet interdum nisi venenatis non. Curabitur et tellus a ligula auctor porta eu facilisis diam. Cras quis malesuada mi, quis dictum erat. Phasellus vel justo nec purus aliquet lacinia a sed dolor. Nam gravida ut mauris ut ultrices.";

        List<User> users = new ArrayList<>(Arrays.asList(
                new User("mainUser", "password1", "Im the main user " + description, "userImage1", "usuario1@urjc.es"),
                new User("mainUser", "password1", "description User1 " + description, "userImageMain", "usuarioMain@urjc.es"),
                new User("user2", "password1", "description User2 " + description, "userImage2", "usuario2@urjc.es"),
                new User("user3", "password1", "description User3 " + description, "userImage3", "usuario3@urjc.es"),
                new User("user4", "password1", "description User4 " + description, "userImage4", "usuario4@urjc.es"),
                new User("user5", "password1", "description User5 " + description, "userImage5", "usuario5@urjc.es"),
                new User("user6", "password1", "description User6 " + description, "userImage6", "usuario6@urjc.es")));
        return users;

    }

    public void createPosts() {
        // Create users Posts
        String content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam nec dictum ex. Sed eu lectus ut velit pharetra dictum quis et nisi. Suspendisse in nisl quam. Vestibulum non dapibus magna. Duis volutpat magna eget venenatis egestas. Fusce tincidunt, neque et finibus suscipit, mi tortor facilisis arcu, eu euismod diam magna non metus. Aliquam blandit sodales dui, sit amet imperdiet ipsum euismod sed. Mauris luctus neque eu nulla posuere, sit amet dignissim augue lacinia. Aliquam viverra ullamcorper lacus, sit amet interdum nisi venenatis non. Curabitur et tellus a ligula auctor porta eu facilisis diam. Cras quis malesuada mi, quis dictum erat. Phasellus vel justo nec purus aliquet lacinia a sed dolor. Nam gravida ut mauris ut ultrices.";

        Post post1, post2, post3;
        for (User user : this.aplicationUsers) {
            post1 = user.createPost("Post1", content, "postImage1");
            post2 = user.createPost("Post2", content, "postImage2");
            post3 = user.createPost("Post3", content, "postImage3");
            user.addPost(post1);
            user.addPost(post2);
            user.addPost(post3);
        }

    }

    public List<Section> createSections() {
        List<Section> sections = new ArrayList<>(Arrays.asList(
                new Section("Reversing", "Análisis y descompilación de binarios para entender su funcionamiento.",
                        "reversing.jpg"),
                new Section("Hacking Web", "Explotación de vulnerabilidades en aplicaciones web.", "hacking_web.jpg"),
                new Section("Escalada de Privilegios en Linux",
                        "Técnicas para obtener mayores privilegios en sistemas Linux.", "escalada_linux.jpg"),
                new Section("Escalada de Privilegios en Windows",
                        "Métodos para obtener acceso administrativo en Windows.", "escalada_windows.jpg"),
                new Section("Hardware Hacking", "Explotación de vulnerabilidades a nivel de hardware.", "hardware.jpg"),
                new Section("WiFi", "Ataques y auditorías de seguridad en redes inalámbricas.", "wifi.jpg"),
                new Section("XSS", "Cross-Site Scripting, inyección de scripts en páginas web.", "xss.jpg"),
                new Section("SQLi", "SQL Injection, explotación de vulnerabilidades en bases de datos.", "sqli.jpg")));
        return sections;
    }

}
