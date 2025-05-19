package es.codeurjc.web.restController;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.web.dto.UserBasicDTO;
import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public Page<UserBasicDTO> getUsers(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return userService.findAllAsBasicDTO(pageable);
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable long id) {
        if (userService.findById(id) != null) {
            return userService.findById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

    }

    @PostMapping("/")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO newUser, HttpServletResponse response) {

        for (UserDTO user : userService.findAllUsers()) {
            if (user.email().equals(newUser.email()) || user.userName().equals(newUser.userName())) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
        }
        User user = new User(newUser.userName(), passwordEncoder.encode(newUser.password()), newUser.email(), "USER");
        userService.save(user);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri(); // URI for the new User
        return ResponseEntity.created(location).body(userService.findById(user.getId()));
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable long id, @RequestBody UserDTO newUserDTO,
            MultipartFile newImagem, HttpServletRequest request) throws IOException, SQLException {
        UserDTO oldUser = userService.findById(id);
        if (oldUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (userService.checkIfTheUserIsFollowed(newUserDTO, request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot update this user");
        }
        return userService.updateApiUser(id, newUserDTO);

    }

        @PutMapping("/{id}")
        public ResponseEntity<Void> updateUser(@PathVariable long id, @RequestBody UserDTO newUserDTO, HttpServletRequest request)
        throws SQLException {
        
            UserDTO oldUser = userService.findById(id);
            if (oldUser == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            if (!userService.checkIsSameUser(id, request)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot update this user");
            }
        
            userService.updateApiUser(id, newUserDTO);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.WWW_AUTHENTICATE,
                            "Bearer error=\"invalid_token\", error_description=\"Updated successfully. Please login again\"")
                    .build();
        }
    
    @DeleteMapping("/{id}")
    public UserDTO deleteUser(@PathVariable long id, HttpServletRequest request) {
        UserDTO user = userService.findById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (userService.checkIfTheUserIsFollowed(user, request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot  delete this user");
        }
        return userService.deleteUser(user);
    }

    @PostMapping("/{id}/followings")
    public UserDTO followUser(@PathVariable long id, @RequestBody UserDTO userToFollowDTO, HttpServletRequest request) {
        UserDTO user = userService.findById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (userService.checkIsSameUser(user.id(), request) && userToFollowDTO.id() != 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot  follow yourself");
        }
        userService.followUser(userToFollowDTO, request);
        return user;
    }

    @DeleteMapping("/{id}/followings")
    public UserDTO unfollowUser(@PathVariable long id, @RequestBody UserDTO userToUnFollowDTO,
            HttpServletRequest request) {
        UserDTO userDTO = userService.getUserById(id);

        if (userDTO == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (userService.checkIfTheUserIsFollowed(userDTO, request)) {
            if (userService.existsById(userToUnFollowDTO.id()) && userDTO.followings().contains(userToUnFollowDTO)) {
                userService.unfollowUser(userToUnFollowDTO, request);
                return userDTO;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Object> createUserImage(
            @PathVariable long id, @RequestParam MultipartFile imageFile, HttpServletRequest request)
            throws IOException {
        UserDTO user = userService.findById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (!userService.checkIsSameUser(user.id(), request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot create an image for this user");
        }
        URI location = fromCurrentRequest().build().toUri();
        userService.createUserImage(id, location, imageFile.getInputStream(), imageFile.getSize());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Object> getUserImage(@PathVariable long id)
            throws SQLException, IOException {
        UserDTO user = userService.findById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (user.image() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User image not found");

        }
        Resource image = userService.getUserImage(id);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(image);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Object> replaceUserImage(@PathVariable long id, @RequestParam MultipartFile imageFile,
            HttpServletRequest request)
            throws IOException {
        UserDTO user = userService.findById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (user.image() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User image not found");
        }

        if (!userService.checkIsSameUser(user.id(), request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit an image for this user");
        }
        userService.replaceUserImage(id, imageFile.getInputStream(), imageFile.getSize());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Object> deleteUserImage(@PathVariable long id, HttpServletRequest request)
            throws IOException {
        UserDTO user = userService.findById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (user.image() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User image not found");
        }

        if (!userService.checkIsSameUser(user.id(), request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete an image for this user");
        }
        userService.deleteUserImage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/CV")
    public ResponseEntity<Object> getUserCV(@PathVariable long id) throws IOException {
        UserDTO user = userService.findById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (user.cvFilePath() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User CV not found");
        }
        ResponseEntity<Resource> cv = userService.downloadCV(id);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/pdf").body(cv);

    }

    @PutMapping("/{id}/CV")
    @PostMapping("/{id}/CV")
    public ResponseEntity<Object> createUserCV(@PathVariable long id, @RequestParam MultipartFile file,
            HttpServletRequest request)
            throws IOException {
        UserDTO user = userService.findById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (!userService.checkIsSameUser(user.id(), request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot create a CV for this user");
        }
        URI location = fromCurrentRequest().build().toUri();
        userService.uploadCv(id, file);
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}/CV")
    public ResponseEntity<Object> deleteUserCV(@PathVariable long id, HttpServletRequest request) throws IOException {
        UserDTO user = userService.findById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (user.cvFilePath() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User CV not found");
        }

        if (!userService.checkIsSameUser(user.id(), request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete a CV for this user");
        }
        userService.deleteCv(id);
        return ResponseEntity.noContent().build();
    }
}