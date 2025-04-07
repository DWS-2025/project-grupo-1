package es.codeurjc.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.dto.SectionDTO;
import es.codeurjc.web.dto.UserBasicDTO;
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

    
    public UserDTO getLoggedUser() {
        return toDTO(userRepository.findByUserName("mainUser"));
    }

    public UserBasicDTO getLoggedUserBasic() {
        return toBasicDTO(userRepository.findByUserName("mainUser"));
    }

    public Collection<UserDTO> findAllUsers() {
        return toDTOs(userRepository.findAll());
    }

    public Page<UserDTO> findAllAsDTO(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<UserBasicDTO> findAllAsBasicDTO(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toBasicDTO);
    }

    public UserDTO save(UserDTO userDTO){
        User user = toDomain(userDTO);
        this.save(user);
        return userDTO;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean existsById(long id) {
        return userRepository.existsById(id);
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

    public UserBasicDTO findBasicById(long id) {
        return toBasicDTO(userRepository.findById(id).orElseThrow());
    }

    public UserDTO findByUserName(String userName) {
        return toDTO(userRepository.findByUserName(userName));
    }

    public Boolean isLogged(UserDTO userDTO) {
        User user = toDomain(userDTO);
        return userRepository.findAll().get(0).equals(user);
    }

    public UserDTO deleteUser(UserDTO userDTO) {
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
        return toDTO(userToDelete);
    }

    public UserDTO uptadeUser(UserDTO userDTO, String newUserName, String description, MultipartFile userImage) throws IOException {
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
        return toDTO(user);
    }
    public void unfollowUser(UserBasicDTO userToUnfollowDTO, UserBasicDTO loggedUserDTO){
        User userToUnfollow = toDomain(userToUnfollowDTO);
        User loggedUser = toDomain(loggedUserDTO);
        loggedUser.unfollow(userToUnfollow);
        userRepository.save(loggedUser);
        userRepository.save(userToUnfollow);
    }
    public void followUser(UserBasicDTO userToFollowDTO, UserBasicDTO loggedUserDTO){
        User userToFollow = toDomain(userToFollowDTO);
        User loggedUser = toDomain(loggedUserDTO);
        loggedUser.follow(userToFollow);
        userRepository.save(loggedUser);
        userRepository.save(userToFollow);
    }

    private UserDTO toDTO(User user) {
        return mapper.toDTO(user);
    }

    private UserBasicDTO toBasicDTO(User user) {
        return mapper.toBasicDTO(user);
    }

    private User toDomain(UserDTO userDTO) {
        return mapper.toDomain(userDTO);
    }

    private User toDomain(UserBasicDTO userBasicDTO) {
        return mapper.toBasicDomain(userBasicDTO);
    }

    private Collection<UserDTO> toDTOs(List<User> users) {
        return mapper.toDTOs(users);
    }

    private Collection<User> toDomains(Collection<UserDTO> userDTOs) {
        return mapper.toDomains(userDTOs);
    }

    public Collection<User> getUsersFromUserNamesList(String[] contributorNames) {
        Collection<User> users = new ArrayList<>();
        User user;

        for (String colaborator : contributorNames) {
            user = userRepository.findByUserName(colaborator);
            if (user != null) {
                users.add(user);
            }
        }
        
        return users;
    }


    public SectionDTO followSection(UserDTO userDTO, SectionDTO sectionDTO){
        User user = toDomain(userDTO);
        Section section = sectionService.toDomain(sectionDTO);
        
        user.followSection(section);
        userRepository.save(user);
        sectionService.saveSection(section);
        
        return sectionDTO;
    }

    public SectionDTO unfollowSection (UserDTO userDTO, SectionDTO sectionDTO){
        User user = toDomain(userDTO);
        Section section = sectionService.toDomain(sectionDTO);

        user.unfollowSection(section);
        userRepository.save(user);
        sectionService.saveSection(section);

        return sectionDTO;
    }
}
