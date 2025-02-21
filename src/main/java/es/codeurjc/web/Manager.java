package es.codeurjc.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class Manager {

    private User mainUser;
    private List<User> aplicationUsers;
    private List<Section> sections;
    //añadir lista de posts para pasarle al rankingManger
    private List<Post> aplicationPosts;

    public Manager() {
        aplicationUsers = new ArrayList<>(); 
        sections = new ArrayList<>();
        aplicationPosts = new ArrayList<>();
        this.init();
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
    public List<Section> getSections() {
        return this.sections;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void deleteSection(Section section) {
        sections.remove(section);
    }

    public List<User> getAplicationUsers(){
        return this.aplicationUsers;
    }

    public List<Post> getAplicationPosts(){
        List<User> totalUsers = getAplicationUsers();
        List<Post> userPosts;

        for (User user : totalUsers){
            userPosts = new ArrayList<>();
            userPosts = user.getPosts();

            for (Post userPost : userPosts){ //añadir cada post a la lista de todos los post publicados de la app
               aplicationPosts.add(userPost);
            }
        }

        return aplicationPosts;
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
        this.mainUser = this.aplicationUsers.get(0);
        // Creates a list of posts and add them to the users(all users have the same posts)
        this.createPosts();
        // Creates  the sections and make the users follow them
        this.sections = this.createSections();
        this.followSectionAutomated();
        // Make the users follow each other
        this.followUsersAutomated();
        // Make the users comment on their posts
        this.commentOnPostsAutomated();
    }

    public static List<User> createUsers() {
        String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam nec dictum ex. Sed eu lectus ut velit pharetra dictum quis et nisi. Suspendisse in nisl quam. Vestibulum non dapibus magna. Duis volutpat magna eget venenatis egestas. Fusce tincidunt, neque et finibus suscipit, mi tortor facilisis arcu, eu euismod diam magna non metus. Aliquam blandit sodales dui, sit amet imperdiet ipsum euismod sed. Mauris luctus neque eu nulla posuere, sit amet dignissim augue lacinia. Aliquam viverra ullamcorper lacus, sit amet interdum nisi venenatis non. Curabitur et tellus a ligula auctor porta eu facilisis diam. Cras quis malesuada mi, quis dictum erat. Phasellus vel justo nec purus aliquet lacinia a sed dolor. Nam gravida ut mauris ut ultrices.";

        List<User> users = new ArrayList<>(Arrays.asList(
                new User("mainUser", "password1", "Im the main user " + description, "userImage1", "usuario1@urjc.es"),
                new User("user1", "password1", "description User1 " + description, "userImageMain", "usuarioMain@urjc.es"),
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

        Post post1, post2, post3, post4, post5, post6;
        for (User user : this.aplicationUsers) {
            post1 = user.createPost("Post1", content, "postImage1");
            post2 = user.createPost("Post2", content, "postImage2");
            post3 = user.createPost("Post3", content, "postImage3");
            post4 = user.createPost("Post4", content, "postImage4");
            post5 = user.createPost("Post5", content, "postImage5");
            post6 = user.createPost("Post6", content, "postImage6");
            user.addPost(post1);
            user.addPost(post2);
            user.addPost(post3);
            user.addPost(post4);
            user.addPost(post5);
            user.addPost(post6);

        }

    }

    public List<Section> createSections() {
        List<Section> sections = new ArrayList<>(Arrays.asList(
                new Section("Reversing", "Análisis y descompilación de binarios para entender su funcionamiento.",
                        "reversing.png", 4.5f),
                new Section("Hacking Web", "Explotación de vulnerabilidades en aplicaciones web.", "hacking_web.png", 4.0f),
                new Section("Escalada de Privilegios",
                        "Métodos para obtener acceso administrativo en Windows.", "escalada_windows.jpeg",3.0f),
                new Section("Hardware Hacking", "Explotación de vulnerabilidades a nivel de hardware.", "hardware.jpeg", 2.5f),
                new Section("WiFi", "Ataques y auditorías de seguridad en redes inalámbricas.", "wifi.jpg", 2.0f)));
        return sections;
    }

    // *** REVISAR ESTOS DOS METODOS (funcionan pero no se si son los mas optimos) ***
    //This method will make each user follow a random number of sections (at least one) from the available sections in Manager.sections.
    public void followSectionAutomated() {
        Random random = new Random();
        for (User user : this.aplicationUsers) {
            int numberOfSectionsToFollow = random.nextInt(this.sections.size()) + 1; // At least one section
            Set<Section> followedSections = new HashSet<>();
            for (int i = 0; i < numberOfSectionsToFollow; i++) {
                Section section;
                do {
                    section = this.sections.get(random.nextInt(this.sections.size()));
                } while (followedSections.contains(section)); // Ensure a section is not followed more than once
                user.followSection(section);
                followedSections.add(section);
            }
        }
    }
    public void followUsersAutomated() {
        Random random = new Random();
        for (User user : this.aplicationUsers) {
            int numberOfUsersToFollow = random.nextInt(this.aplicationUsers.size() - 1) + 1; // At least one user, excluding self
            Set<User> followedUsers = new HashSet<>();
            for (int i = 0; i < numberOfUsersToFollow; i++) {
                User userToFollow;
                do {
                    userToFollow = this.aplicationUsers.get(random.nextInt(this.aplicationUsers.size()));
                } while (userToFollow.equals(user) || followedUsers.contains(userToFollow)); // Ensure a user does not follow themselves or the same user more than once
                user.follow(userToFollow);
                followedUsers.add(userToFollow);
            }
        }
    }
    public void commentOnPostsAutomated() {
        Random random = new Random();
        for (User user : this.aplicationUsers) {
            for (Post post : user.getPosts()) {
                int numberOfComments = random.nextInt(5) + 1; // At least one comment
                for (int i = 0; i < numberOfComments; i++) {
                    user.comment(post, "This is a comment by " + user.getName());
                }
            }
        }
    }

}