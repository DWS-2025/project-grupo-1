package es.codeurjc.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;

/**
 * Service implementation of {@link UserDetailsService} that loads user-specific data.
 * This service retrieves user details from the {@link UserRepository} based on the provided username.
 * It is used by Spring Security for authentication and authorization.
 *
 * <p>
 * The {@code loadUserByUsername} method fetches a {@link User} entity from the repository,
 * converts its roles to {@link GrantedAuthority} objects, and returns a Spring Security
 * {@link org.springframework.security.core.userdetails.User} instance.
 * </p>
 *
 * <p>
 * If the user is not found, a {@link UsernameNotFoundException} is thrown.
 * </p>
 *
 * @author Grupo 1
 */
@Service
public class RepositoryUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> roles = new ArrayList<>();
        for (String role : user.getRols()) {
            roles.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        return new org.springframework.security.core.userdetails.User(user.getUserName(),
                user.getPassword(), roles);

    }
}
