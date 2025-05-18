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
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import jakarta.servlet.http.HttpServletRequest;

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

    public UserDTO getLoggedUser(String userName) {
        return toDTO(userRepository.findByUserName(userName).get());
    }

    public User getLoggedUserDomain(String userName) {
        return userRepository.findByUserName(userName).get();
    }

    public UserBasicDTO getLoggedUserBasic(String userName) {
        return toBasicDTO(userRepository.findByUserName(userName).get());
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

    public boolean checkIsSameUser(Long id, HttpServletRequest request) {

        UserDTO loggedUser = getLoggedUser(request.getUserPrincipal().getName());
        UserDTO userToEdit = findById(id);

        if (loggedUser.id().equals(userToEdit.id()) || loggedUser.userName().equals("Admin")) {
            return true;
        } else {
            return false;
        }

    }

    public UserDTO getUserById(long id) {
        return toDTO(userRepository.findById(id).get());
    }

    public UserDTO findById(long id) {
        return toDTO(userRepository.findById(id).orElseThrow());
    }

    public User findByIdDomain(long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public UserBasicDTO findBasicById(long id) {
        return toBasicDTO(userRepository.findById(id).orElseThrow());
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName).orElseThrow();
    }

    public UserDTO findByUserNameDTO(String userName) {
        return toDTO(userRepository.findByUserName(userName).orElseThrow());
    }

    public UserBasicDTO findByUserNameBasicDTO(String userName) {
        return toBasicDTO(userRepository.findByUserName(userName).orElseThrow());
    }

    public Boolean isLogged(UserDTO userDTO) {
        User user = toDomain(userDTO);
        return userRepository.findAll().get(0).equals(user);
    }

    public UserDTO findByUserNameAuth(String username) {
        return userRepository.findByUserName(username)
                .map(mapper::toDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
                    postService.deletePost(post.getId());
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

    public void updateWebUser(long id, String userName, String description, MultipartFile image) {
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        User user = userRepository.findById(id).orElseThrow();

        if (userName != null && !userName.isEmpty()) {
            userName = policy.sanitize(user.getUserName());
            user.setUserName(userName);
        }
        if (description != null && !description.isEmpty()) {
            description = policy.sanitize(user.getDescription());
            user.setDescription(description);
        }
        if (image != null && !image.isEmpty()) {
            try {
                user.setUserImage(BlobProxy.generateProxy(image.getInputStream(), image.getSize()));
                user.setImage(image.getOriginalFilename());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        userRepository.save(user);
    }

    public UserDTO updateApiUser(long id, UserDTO updatedUserDTO) throws SQLException {
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        User oldUser = userRepository.findById(id).orElseThrow();
        User updatedUser = toDomain(updatedUserDTO);
        updatedUser.setId(id);

        String userName = policy.sanitize(updatedUser.getUserName());
        if (userName != null && !userName.isEmpty()) {
            oldUser.setUserName(userName);
        }

        String description = policy.sanitize(updatedUser.getDescription());
        if (description != null && !description.isEmpty()) {
            oldUser.setDescription(description);
        }

        String email = policy.sanitize(updatedUser.getEmail());
        if (email != null && !email.isEmpty()) {
            oldUser.setEmail(email);
        }

        String password = policy.sanitize(updatedUser.getPassword());
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

    public UserDTO updateApiUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id).orElseThrow();

        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        String sanitizedUsername = policy.sanitize(userDTO.userName());
        String sanitizedDescription = policy.sanitize(userDTO.description());
        String sanitizedEmail = policy.sanitize(userDTO.email());

        user.setUserName(sanitizedUsername);
        user.setDescription(sanitizedDescription);
        user.setEmail(sanitizedEmail);

        return toDTO(userRepository.save(user));
    }

    public void unfollowUser(UserDTO userToUnfollowDTO, HttpServletRequest request) {
        User userToUnfollow = userRepository.findById(userToUnfollowDTO.id()).orElseThrow();
        User loggedUser = getLoggedUserDomain(request.getUserPrincipal().getName());
        loggedUser.unfollow(userToUnfollow);
        userRepository.save(loggedUser);
        userRepository.save(userToUnfollow);
    }

    public void followUser(UserDTO userToFollowDTO, HttpServletRequest request) {
        User userToFollow = userRepository.findById(userToFollowDTO.id()).orElseThrow();
        User loggedUser = getLoggedUserDomain(request.getUserPrincipal().getName());
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
        return mapper.toDomain(userBasicDTO);
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
            user = userRepository.findByUserName(colaborator).get();
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
        // Find the user by ID or throw an exception if not found
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Check if the user has a CV file path
        if (user.getCvFilePath() == null) {
            throw new NoSuchElementException("CV not found for this user");
        }

        // Build the file path using the base directory and the relative path from the
        // database
        File file = new File(BASE_DIRECTORY, user.getCvFilePath());

        // Canonicalize the file path and verify it is within the allowed directory
        String expectedBasePath = new File(BASE_DIRECTORY, CV_DIRECTORY).getCanonicalPath();
        String fileCanonicalPath = file.getCanonicalPath();
        System.out.println("Expected base path: " + expectedBasePath);
        System.out.println("File canonical path: " + fileCanonicalPath);

        if (!fileCanonicalPath.startsWith(expectedBasePath)) {
            throw new SecurityException("Invalid file path detected, go hack other page script kiddie");
        }

        // Check if the file exists
        if (!file.exists()) {
            throw new NoSuchElementException("CV file not found at: " + fileCanonicalPath);
        }

        // Create a resource for the file
        Resource resource = new InputStreamResource(new FileInputStream(file));

        // Return the file as a downloadable response
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    public void uploadCv(Long userId, MultipartFile file) throws IOException {
        // Find the user by ID or throw an exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        String originalFileName = file.getOriginalFilename();
        // Check if the file name is not null and contains valid characters
        if (!originalFileName.matches("[a-zA-Z0-9._\\-() ]+")) {
            throw new IllegalArgumentException("El archivo contiene caracteres no válidos");
        }
        if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Solo se permiten archivos PDF");
        }
        if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
            throw new IllegalArgumentException("El archivo debe ser un PDF");
        }
        // Build the file path using the base directory and the sanitized file name
        File destinationFile = new File(BASE_DIRECTORY + File.separator + CV_DIRECTORY, originalFileName);

        // Canonicalize the expected base path
        String expectedBasePath = new File(BASE_DIRECTORY, CV_DIRECTORY).getCanonicalPath();
        System.out.println("Expected base path: " + expectedBasePath);
        System.out.println("Destination file canonical path: " + destinationFile.getCanonicalPath());

        // Verify that the canonical path of the destination file starts with the
        // expected base path
        if (!destinationFile.getCanonicalPath().startsWith(expectedBasePath)) {
            throw new SecurityException("Invalid file path detected, go hack other page script kiddie");
        }

        // Create the directories if they do not exist
        destinationFile.getParentFile().mkdirs();

        // Save the file to the destination
        file.transferTo(destinationFile);

        // Save only the relative path of the file in the database
        user.setCvFilePath(CV_DIRECTORY + originalFileName);
        userRepository.save(user);
    }

    public boolean checkIfTheUserIsFollowed(UserDTO userToCheck, HttpServletRequest request) {
        User user = getLoggedUserDomain(request.getUserPrincipal().getName());
        User userToCheckDomain = userRepository.findById(userToCheck.id()).orElseThrow();
        if (user.getFollowings().contains(userToCheckDomain)) {
            return true;
        } else {
            return false;
        }
    }

    public Collection<UserDTO> getOnlyUsersRole(HttpServletRequest request) {
        UserDTO loggedUser = getLoggedUser(request.getUserPrincipal().getName());
        Collection<UserDTO> users = findAllUsers();
        Collection<UserDTO> onlyUsers = new ArrayList<>();

        for (UserDTO user : users) {
            if (!user.userName().equals(loggedUser.userName()) && !user.userName().equals("Admin")) {
                onlyUsers.add(user);
            }
        }

        return onlyUsers;
    }

}
