package es.codeurjc.web.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    PostService postService;

    @Autowired
    SectionService sectionService;

    /*private User loggedUser;  THIS WILL BE USED IN THE NEXT PHASE OF THE PROJECT 

    public void setLoggedUser(HttpSession session, User user){
        session.setAttribute("User", user);
        loggedUser = user;
    }

    public User getLoggedUser(){
        return loggedUser;
    }*/
    public User getLoggedUser() {
        return userRepository.findByUserName("mainUser");
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User getUserById(long id) {
        return userRepository.findById(id).get();
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public Boolean isLogged(User user) {
        return userRepository.findAll().get(0).equals(user);
    }

    public void deleteUser(User userToDelete) {
        long id = userToDelete.getId();
        if (id != 1) {
            // We break the relationship between the posts and the sections
            if(!userToDelete.getPosts().isEmpty())
            {
            for (Post post : userToDelete.getPosts()) {
                List<Section> sections = post.getSections();
                for (Section section : sections) {
                    section.deletePost(post);
                    sectionService.saveSection(section);
                }
            }
        }
        if(!userToDelete.getCollaboratedPosts().isEmpty())
        {
            // We break the relationship between the posts and the contributors
            for (Post post : userToDelete.getCollaboratedPosts()) {
                post.getContributors().remove(userToDelete);
                postService.save(post);
            }
        }
        if(!userToDelete.getFollowers().isEmpty())
        {
            // We break the relationship between the user and the followers
            for (User follower : userToDelete.getFollowers()) {
                follower.getFollowings().remove(userToDelete);
                userRepository.save(follower);
            }
        }
        if(!userToDelete.getFollowings().isEmpty())
        {
            // We break the relationship between the user and the followings
            for (User following : userToDelete.getFollowings()) {
                following.getFollowers().remove(userToDelete);
                userRepository.save(following);
            }

            
            
        }
    }
        // We finally delete the user
        userRepository.deleteById(id);
    }
}
