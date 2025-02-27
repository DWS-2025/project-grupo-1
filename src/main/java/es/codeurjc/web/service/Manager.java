package es.codeurjc.web.service;

import java.util.ArrayList;
import java.util.Arrays;
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
import es.codeurjc.web.Repository.UserRepository;
import es.codeurjc.web.Repository.CommentRepository;
import es.codeurjc.web.Repository.SectionRepository;

@Component
public class Manager {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostService postRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private CommentRepository commentRepository;

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
		User user6 = new User("user6", "Contraseña5", "user6r@gmail.com");


		// Some examples of posts and comments
		Post post = new Post();
		
		Comment comment = new Comment();
		comment.setAuthor();
		michel.getComments().add();
		post.getComments().add();
		commentRepository.save();

		Comment comment2 = new Comment();
		comment2.setAuthor();
		carlos.getComments().add();
		post.getComments().add();
		commentRepository.save();

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
		postRepository.save(user6);
		sectionRepository.save(defaultSection1);
		sectionRepository.save(defaultSection2);
		sectionRepository.save(defaultSection3);
		sectionRepository.save(defaultSection4);
		sectionRepository.save(defaultSection5);
	}
} 
