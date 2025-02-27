package es.codeurjc.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.web.Model.Comment;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.Model.Section;
import es.codeurjc.web.Model.User;
import jakarta.annotation.PostConstruct;
import es.codeurjc.web.Repository.CommentRepository;
import es.codeurjc.web.Repository.SectionRepository;

@Component
public class Manager {
    @Autowired
    private UserService userRepository;
    @Autowired
    private PostService postRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private CommentRepository commentRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private SectionService sectionService;

    @PostConstruct
	public void init() {

		// Default user
		User mainUser = new User("mainUser", "ContraseñaSegura", "mainUser@gmail.com");
		// Other users
        User user1 = new User("user1", "Contraseña1", "user1@gmail.com");
        User user2 = new User("user2", "Contraseña2", "user2@gmail.com");
        User user3  = new User("user3", "Contraseña3", "user3@gmail.com");
        User user4 = new User("user4", "Contraseña4", "user4@gmail.com");
        User user5 = new User("user5", "Contraseña5", "user5@gmail.com");
		User user6 = new User("user6", "Contraseña6", "user6@gmail.com");


		// Some examples of posts and comments
		Post post = new Post("HOLA", "Esto es un post de prueba", "/assets/images/stream-01.jpg");
		Post post2 = new Post("HOLA 2", "Esto es un post de prueba 2", "/assets/images/stream-02.jpg");
		Post post3 = new Post("HOLA 3", "Esto es un post de prueba 3", "/assets/images/stream-03.jpg");
		Post post4 = new Post("HOLA 4", "Esto es un post de prueba 4", "/assets/images/stream-04.jpg");
		Post post5 = new Post("HOLA 5", "Esto es un post de prueba 5", "/assets/images/stream-05.jpg");
		Post post6 = new Post("HOLA 6", "Esto es un post de prueba 6", "/assets/images/stream-06.jpg");
		Post post7 = new Post("HOLA 7", "Esto es un post de prueba 7", "/assets/images/stream-07.jpg");
		Post post8 = new Post("HOLA 8", "Esto es un post de prueba 8", "/assets/images/stream-08.jpg");
		
		Comment comment = new Comment();
		comment.setOwner(mainUser);
		mainUser.getComments().add(comment);
		post.getComments().add(comment);
		commentRepository.save(comment);

		Comment comment2 = new Comment();
		comment2.setOwner(user1);
		user1.getComments().add(comment2);
		post.getComments().add(comment2);
		commentRepository.save(comment2);

		//Create new sections
        Section defaultSection1 = new Section("Reversing","Análisis y descompilación de binarios para entender su funcionamiento.", "reversing.png");
        Section defaultSection2 = new Section("Hacking Web", "Explotación de vulnerabilidades en aplicaciones web.","hacking_web.png");
        Section defaultSection3 = new Section("Escalada de Privilegios","Métodos para obtener acceso administrativo en Windows.", "escalada_windows.jpeg");
        Section defaultSection4 = new Section("Hardware Hacking","Explotación de vulnerabilidades a nivel de hardware.", "hardware.jpeg");
        Section defaultSection5 = new Section("WiFi", "Ataques y auditorías de seguridad en redes inalámbricas.","wifi.jpg");

		userRepository.save(mainUser);
		userRepository.save(user1);
		userRepository.save(user2);
		userRepository.save(user3);
		userRepository.save(user4);
		userRepository.save(user5);
		userRepository.save(user6);
		postRepository.save(post);
		postRepository.save(post2);
		postRepository.save(post3);
		postRepository.save(post4);
		postRepository.save(post5);
		postRepository.save(post6);
		postRepository.save(post7);
		postRepository.save(post8);
		sectionRepository.save(defaultSection1);
		sectionRepository.save(defaultSection2);
		sectionRepository.save(defaultSection3);
		sectionRepository.save(defaultSection4);
		sectionRepository.save(defaultSection5);
	}


	public void followSectionAutomated() {
		Random random = new Random();
		List<Section> sections = sectionService.findAll();
		for (int i = 1; i <= userService.findAllUsers().size(); i++) {
			if (userService.getUserById(i) != null) {
				int numberOfSectionsToFollow = random.nextInt(sections.size() - 1) + 1; // At least one section
				Set<Section> followedSections = new HashSet<>();
				for (int j = 0; j < numberOfSectionsToFollow; j++) {
					Section sectionToFollow;
					do {
						sectionToFollow = sections.get(random.nextInt(sections.size()));
					} while (followedSections.contains(sectionToFollow)); // Ensure a user does not follow the same section more than once
					userService.getUserById(i).followSection(sectionToFollow);
					followedSections.add(sectionToFollow);
				}
			}
		}
	}

	public void followUsersAutomated() {
		Random random = new Random();
		List<User> users = userService.findAllUsers();
		for (int i = 1; i <= userService.findAllUsers().size(); i++) {
			int numberOfUsersToFollow = random.nextInt(users.size() - 1) + 1; 
			Set<User> followedUsers = new HashSet<>();
			
			for (int j = 0; j < numberOfUsersToFollow; j++) {
				User userToFollow;
				do {
					userToFollow = users.get(random.nextInt(users.size()));
				} while (followedUsers.contains(userToFollow) || userService.getUserById(i).equals(userToFollow)); // Evitar seguir al mismo usuario
				
				userService.getUserById(i).follow(userToFollow);
				followedUsers.add(userToFollow);
			}
		}
	}
}