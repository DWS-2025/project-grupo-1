package es.codeurjc.web.restController;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.web.dto.UserBasicDTO;
import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService UserService;

    @Autowired
    private NoSuchElementExceptionControllerAdvice advicer;

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
    public UserDTO updateUser(@PathVariable long id, @RequestBody UserDTO oldUserDTO,
            MultipartFile newImage) throws IOException {

        UserDTO newUser = UserService.findById(id);
        return UserService.updateUser(newUser, oldUserDTO, newImage);
        
    }

    @DeleteMapping("/{id}")
    public UserDTO deleteUser(@PathVariable long id) {

        UserDTO User = UserService.findById(id);

        if (User == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        } else {
            return UserService.deleteUser(User);
        }

    }

    @PostMapping("/{id}/followings")
    public UserBasicDTO followUser(@PathVariable long id, @RequestBody UserBasicDTO userToFollowDTO) {
        UserBasicDTO userDTO = UserService.findBasicById(id);

        UserService.followUser(userToFollowDTO, userDTO);

        return userDTO;
    }

    @DeleteMapping("/{id}/followings")
    public UserBasicDTO unfollowUser(@PathVariable long id, @RequestBody UserBasicDTO userToUnFollowDTO) {
        UserBasicDTO userDTO = UserService.findBasicById(id);

        if (UserService.existsById(userToUnFollowDTO.id()) && userDTO.followings().contains(userToUnFollowDTO)) {
            UserService.unfollowUser(userToUnFollowDTO, userDTO);

        return userDTO;
    }
}
