/*package es.codeurjc.web.service;

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


@Component
public class Manager {
    @Autowired
    private UserService userRepository;
    @Autowired
    private PostService postRepository;
    @Autowired
    private SectionService sectionRepository;
    @Autowired
    private CommentService commentRepository;

    @PostConstruct
	public void init() {

		// Default user
		User michel = new User("mainUser", "ContraseñaSegura", "mainUser@gmail.com");

		// Other users
		User carlos = new User();

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

		postRepository.save();
		userRepository.save();
		userRepository.save();
	}
} */
