package es.codeurjc.web.service;

import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.dto.UserMapper;
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

    @Autowired
    private UserMapper mapper;

    /*private User loggedUser;  THIS WILL BE USED IN THE NEXT PHASE OF THE PROJECT 

    public void setLoggedUser(HttpSession session, User user){
        session.setAttribute("User", user);
        loggedUser = user;
    }

    public User getLoggedUser(){
        return loggedUser;
    }*/

    // idk if we need to return a UserDTO in this case, it conflicts with postService save method and the CommentService
    // maybe we can do 2 methods, one for the services and one for the controller, or just have 2 mappers on those services
    public UserDTO getLoggedUser() {
        return toDTO(userRepository.findByUserName("mainUser"));
    }

    public Collection<UserDTO> findAllUsers() {
        return toDTOs(userRepository.findAll());
    }

    public void save(User user) {
        userRepository.save(user);
    }

    
    public void saveUserWithImage(User user, MultipartFile imageFile) throws IOException {

        if (!imageFile.isEmpty()) {
            byte[] imageBytes = imageFile.getBytes();
            user.setUserImage(BlobProxy.generateProxy(imageBytes));
        }

        this.save(user);
    }

    public UserDTO getUserById(long id) {
        return toDTO(userRepository.findById(id).get());
    }

    public UserDTO findById(long id) {
        return toDTO(userRepository.findById(id).orElseThrow());
    }

    public UserDTO findByUserName(String userName) {
        return toDTO(userRepository.findByUserName(userName));
    }

    public Boolean isLogged(UserDTO userDTO) {
        User user = toDomain(userDTO);
        return userRepository.findAll().get(0).equals(user);
    }

    public void deleteUser(UserDTO userDTO) {
        User userToDelete = toDomain(userDTO);
        long id = userToDelete.getId();
        if (id != 1) {
            // We break the relationship between the posts and the sections
            if (!userToDelete.getPosts().isEmpty()) {
                for (Post post : userToDelete.getPosts()) {
                    List<Section> sections = post.getSections();
                    for (Section section : sections) {
                        section.deletePost(post);
                        sectionService.saveSection(section);
                    }
                }
            }
            if (!userToDelete.getCollaboratedPosts().isEmpty()) {
                // We break the relationship between the posts and the contributors
                for (Post post : userToDelete.getCollaboratedPosts()) {
                    post.getContributors().remove(userToDelete);
                    postService.saveForInit(post);
                }
            }
            if (!userToDelete.getFollowers().isEmpty()) {
                // We break the relationship between the user and the followers
                for (User follower : userToDelete.getFollowers()) {
                    follower.getFollowings().remove(userToDelete);
                    userRepository.save(follower);
                }
            }
            if (!userToDelete.getFollowings().isEmpty()) {
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

    public void uptadeUser(UserDTO userDTO, String newUserName, String description, MultipartFile userImage) throws IOException {
        User user = toDomain(userDTO);
        
        if (newUserName != null && !newUserName.isEmpty()) {
            user.setName(newUserName);
        }

        if (description != null && !description.isEmpty()) {
            user.setDescription(description);
        }
        if (userImage != null && !userImage.isEmpty()) {
            this.saveUserWithImage(user, userImage);
        }
        this.save(user);
    }
    public void unfollowUser(UserDTO userToUnfollowDTO){
        User userToUnfollow = toDomain(userToUnfollowDTO);
        User loggedUser = toDomain(getLoggedUser());
        loggedUser.unfollow(userToUnfollow);
        userRepository.save(loggedUser);
        userRepository.save(userToUnfollow);
    }
    public void followUser(UserDTO userToFollowDTO){
        User userToFollow = toDomain(userToFollowDTO);
        User loggedUser = toDomain(getLoggedUser());
        loggedUser.follow(userToFollow);
        userRepository.save(loggedUser);
        userRepository.save(userToFollow);
    }

    private UserDTO toDTO(User user) {
        return mapper.toDTO(user);
    }

    private User toDomain(UserDTO userDTO) {
        return mapper.toDomain(userDTO);
    }

    private Collection<UserDTO> toDTOs(List<User> users) {
        return mapper.toDTOs(users);
    }

    private Collection<User> toDomains(Collection<UserDTO> userDTOs) {
        return mapper.toDomains(userDTOs);
    }

    public List<User> getUsersFromUserNamesList(String[] contributorNames) {
        List<User> users = new ArrayList<>();
        User user;

        for (String colaborator : contributorNames) {
            user = findByUserName(colaborator);
            if (user != null) {
                users.add(user);
            }
        }
        
        return users;
    }

}
