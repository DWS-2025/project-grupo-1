package es.codeurjc.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.model.User;
import jakarta.annotation.PostConstruct;


@Service
public class Manager {
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
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
	

		//Create new sections
        Section defaultSection1 = new Section("Reversing","Análisis y descompilación de binarios para entender su funcionamiento.", "reversing.png");
        Section defaultSection2 = new Section("Hacking Web", "Explotación de vulnerabilidades en aplicaciones web.","hacking_web.png");
        Section defaultSection3 = new Section("Escalada de Privilegios","Métodos para obtener acceso administrativo en Windows.", "escalada_windows.jpeg");
        Section defaultSection4 = new Section("Hardware Hacking","Explotación de vulnerabilidades a nivel de hardware.", "hardware.jpeg");
        Section defaultSection5 = new Section("WiFi", "Ataques y auditorías de seguridad en redes inalámbricas.","wifi.jpg");

		userService.save(mainUser);
		userService.save(user1);
		userService.save(user2);
		userService.save(user3);
		userService.save(user4);
		userService.save(user5);
		userService.save(user6);

		postService.saveOtherUsersPost(post, mainUser);
		postService.saveOtherUsersPost(post2, mainUser);
		postService.saveOtherUsersPost(post3, user2);
		postService.saveOtherUsersPost(post4, user2);
		postService.saveOtherUsersPost(post5, user3);
		postService.saveOtherUsersPost(post6, user3);
		postService.saveOtherUsersPost(post7, user4);
		postService.saveOtherUsersPost(post8, user4);
		
		sectionService.saveSection(defaultSection5);
		sectionService.saveSection(defaultSection1);
		sectionService.saveSection(defaultSection3);
		sectionService.saveSection(defaultSection2);
		sectionService.saveSection(defaultSection4);

		this.followSectionAutomated();
		this.followUsersAutomated();
		
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