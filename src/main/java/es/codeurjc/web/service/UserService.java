package es.codeurjc.web.service;

import org.springframework.beans.factory.annotation.Autowired;

import es.codeurjc.web.Model.User;
import es.codeurjc.web.Repository.UserRepository;

public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById (long id){
        return userRepository.getUserById(id);
    }

}
