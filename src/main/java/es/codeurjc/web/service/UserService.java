package es.codeurjc.web.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
import jakarta.annotation.PostConstruct;

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

    private static final String BASE_DIRECTORY = System.getProperty("user.dir");
    private static final String CV_DIRECTORY = "/uploads/cvs/";

    @PostConstruct
    public void init() {
        // Create the directory if it doesn't exist
        File directory = new File(CV_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /*
     * private User loggedUser; THIS WILL BE USED IN THE NEXT PHASE OF THE PROJECT
     * 
     * public void setLoggedUser(HttpSession session, User user){
     * session.setAttribute("User", user);
     * loggedUser = user;
     * }
     * 
     * public User getLoggedUser(){
     * return loggedUser;
     * }
     */
    public UserDTO getLoggedUser() {
        return toDTO(userRepository.findByUserName("Admin"));
    }

    public User getLoggedUserDomain() {
        return userRepository.findByUserName("Admin");
    }

    public UserBasicDTO getLoggedUserBasic() {
        return toBasicDTO(userRepository.findByUserName("Admin"));
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

    public UserDTO save(UserDTO userDTO) {
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

    public void saveUserWithImage(UserDTO userDTO, MultipartFile imageFile) throws IOException {
        User user = toDomain(userDTO);
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

    public UserBasicDTO findByUserNameBasicDTO(String userName) {
        return toBasicDTO(userRepository.findByUserName(userName));
    }

    public Boolean isLogged(UserDTO userDTO) {
        User user = toDomain(userDTO);
        return userRepository.findAll().get(0).equals(user);
    }

    public Blob getImage(long id) throws SQLException {
        User user = userRepository.findById(id).orElseThrow();
        if (user.getUserImage() != null) {
            return user.getUserImage();
        } else {
            throw new NoSuchElementException();
        }
    }

    public UserDTO deleteUser(UserDTO userDTO) {

        User userToDelete = userRepository.findById(userDTO.id()).orElseThrow();
        long id = userToDelete.getId();
        // Check if the user is the admin (id = 1)
        // If the user is the admin, do not delete it
        if (id != 1) {

            if (userToDelete.getPosts() != null) {
                List<Post> postsCopy = new ArrayList<>(userToDelete.getPosts());
                for (Post post : postsCopy) {
                    postService.deletePost(post);
                }
            }

            if (userToDelete.getCollaboratedPosts() != null) {
                for (Post post : userToDelete.getCollaboratedPosts()) {
                    post.getContributors().remove(userToDelete);
                    postService.saveForInit(post);
                }
            }
            if (userToDelete.getFollowers() != null) {
                for (User follower : userToDelete.getFollowers()) {
                    follower.getFollowings().remove(userToDelete);
                    userRepository.save(follower);
                }
            }
            if (userToDelete.getFollowings() != null) {
                for (User following : userToDelete.getFollowings()) {
                    following.getFollowers().remove(userToDelete);
                    userRepository.save(following);
                }
            }
        }
        userRepository.deleteById(id);
        return toDTO(userToDelete);
    }

    public UserDTO updateUser(long id, UserDTO updatedUserDTO) throws SQLException {
        User oldUser = userRepository.findById(id).orElseThrow();
        User updatedUser = toDomain(updatedUserDTO);
        updatedUser.setId(id);

        String userName = updatedUser.getUserName();
        if (userName != null && !userName.isEmpty()) {
            oldUser.setUserName(userName);
        }

        String description = updatedUser.getDescription();
        if (description != null && !description.isEmpty()) {
            oldUser.setDescription(description);
        }

        String email = updatedUser.getEmail();
        if (email != null && !email.isEmpty()) {
            oldUser.setEmail(email);
        }

        String password = updatedUser.getPassword();
        if (password != null && !password.isEmpty()) {
            oldUser.setPassword(password);
        }

        if (oldUser.getImage() != null) {
            // Set the image in the updated post
            updatedUser.setUserImage(BlobProxy.generateProxy(
                    oldUser.getUserImage().getBinaryStream(),
                    oldUser.getUserImage().length()));
            updatedUser.setImage(oldUser.getImage());
        }
        userRepository.save(updatedUser);
        return toDTO(updatedUser);
    }

    public void unfollowUser(UserDTO userToUnfollowDTO) {
        User userToUnfollow = userRepository.findById(userToUnfollowDTO.id()).orElseThrow();
        User loggedUser = getLoggedUserDomain();
        loggedUser.unfollow(userToUnfollow);
        userRepository.save(loggedUser);
        userRepository.save(userToUnfollow);
    }

    public void followUser(UserDTO userToFollowDTO) {
        User userToFollow = userRepository.findById(userToFollowDTO.id()).orElseThrow();
        User loggedUser = getLoggedUserDomain();
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

    public SectionDTO followSection(UserDTO userDTO, SectionDTO sectionDTO) {
        User user = userRepository.findById(userDTO.id()).orElseThrow();
        Section section = sectionService.toDomain(sectionDTO);

        user.followSection(section);
        userRepository.save(user);

        return sectionDTO;
    }

    public SectionDTO unfollowSection(UserDTO userDTO, SectionDTO sectionDTO) {
        User user = userRepository.findById(userDTO.id()).orElseThrow();
        Section section = sectionService.toDomain(sectionDTO);

        user.unfollowSection(section);
        userRepository.save(user);

        return sectionDTO;
    }

    public void createUserImage(long id, URI location, InputStream inputStream, long size) {
        User user = userRepository.findById(id).orElseThrow();
        user.setImage(location.toString());
        user.setUserImage(BlobProxy.generateProxy(inputStream, size));
        userRepository.save(user);
    }

    public Resource getUserImage(long id) throws SQLException {
        User user = userRepository.findById(id).orElseThrow();
        if (user.getUserImage() != null) {
            return new InputStreamResource(user.getUserImage().getBinaryStream());
        } else {
            throw new NoSuchElementException();
        }
    }

    public void replaceUserImage(long id, InputStream inputStream, long size) {
        User user = userRepository.findById(id).orElseThrow();
        if (user.getImage() == null) {
            throw new NoSuchElementException();
        }
        user.setUserImage(BlobProxy.generateProxy(inputStream, size));
        userRepository.save(user);
    }

    public void deleteUserImage(long id) {
        User user = userRepository.findById(id).orElseThrow();
        if (user.getImage() == null) {
            throw new NoSuchElementException();
        }
        user.setUserImage(null);
        user.setImage(null);
        userRepository.save(user);
    }

    public ResponseEntity<Resource> downloadCV(long id) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (user.getCvFilePath() == null) {
            throw new NoSuchElementException("CV not found for this user");
        }

        File file = new File(BASE_DIRECTORY + user.getCvFilePath());
        Resource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    public void uploadCv(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Save the file with its original name in the CV directory
        String filePath = BASE_DIRECTORY + CV_DIRECTORY + file.getOriginalFilename();
        File destinationFile = new File(filePath);
        file.transferTo(destinationFile);

        // Update the user's CV file path
        user.setCvFilePath(CV_DIRECTORY + file.getOriginalFilename());
        userRepository.save(user);
    }

    public boolean checkIfTheUserIsFollowed(UserDTO userToCheck) {
        User user = getLoggedUserDomain();
        User userToCheckDomain = userRepository.findById(userToCheck.id()).orElseThrow();
        if (user.getFollowings().contains(userToCheckDomain)) {
            return true;
        } else {
            return false;
        }
    }
}
