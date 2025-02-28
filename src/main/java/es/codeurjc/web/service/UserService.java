package es.codeurjc.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getLoggedUser(){
        return userRepository.findAll().get(1);
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    public void save(User user){
        userRepository.save(user);
    }

    public User getUserById (long id){
        return userRepository.findById(id).get();
    }

    public Boolean isLogged(User user){
        return userRepository.findAll().get(0).equals(user);
    }

}
