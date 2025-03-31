package es.codeurjc.web.restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.web.service.UserService;


@RestController
@RequestMapping("/api/users")

public class UserRestController {
     @Autowired
    private UserService userService;
  
/* 
    @GetMapping("/") 
    public Collection<UserDTO> getAllUsers() {
        return userService.findAllUsers().stream().map(this::toDTO).toList();
    }
    */
}
