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
import es.codeurjc.web.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService UserService;

    @GetMapping("/")
    public Page<UserBasicDTO> getUsers(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return UserService.findAllAsBasicDTO(pageable);
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable long id) {
        if (UserService.findById(id) != null) {
            return UserService.findById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

    }

    @PostMapping("/")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO UserDTO) {
        UserDTO = UserService.save(UserDTO);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(UserDTO.id()).toUri(); // URI for the new User

        return ResponseEntity.created(location).body(UserDTO);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable long id, @RequestBody UserDTO newUserDTO,
            MultipartFile newImage) throws IOException, SQLException {

        UserDTO oldUser = UserService.findById(id);
        if (oldUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        
        return UserService.updateApiUser(id, newUserDTO);
        
    }

    @DeleteMapping("/{id}")
    public UserDTO deleteUser(@PathVariable long id) {
        UserDTO User = UserService.findById(id);
            return UserService.deleteUser(User);
    }

    @PostMapping("/{id}/followings")
    public UserDTO followUser(@PathVariable long id, @RequestBody UserDTO userToFollowDTO) {
        UserDTO userDTO = UserService.getUserById(id);
        UserService.followUser(userToFollowDTO);
        return userDTO;
    }

    @DeleteMapping("/{id}/followings")
    public UserDTO unfollowUser(@PathVariable long id, @RequestBody UserDTO userToUnFollowDTO) {
        UserDTO userDTO = UserService.getUserById(id);
        if (UserService.existsById(userToUnFollowDTO.id()) && userDTO.followings().contains(userToUnFollowDTO)) {
            UserService.unfollowUser(userToUnFollowDTO);
        return userDTO;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");    
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Object> createPostImage(
            @PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
        URI location = fromCurrentRequest().build().toUri();
        UserService.createUserImage(id, location, imageFile.getInputStream(), imageFile.getSize());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Object> getPostImage(@PathVariable long id) 
        throws SQLException, IOException {
        Resource postImage = UserService.getUserImage(id);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(postImage);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Object> replaceUserImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
            throws IOException {
        UserService.replaceUserImage(id, imageFile.getInputStream(), imageFile.getSize());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Object> deleteUserImage(@PathVariable long id) throws IOException {
        UserService.deleteUserImage(id);
        return ResponseEntity.noContent().build();
    }

}
