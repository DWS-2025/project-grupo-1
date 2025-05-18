package es.codeurjc.web.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.dto.PostDTO;
import es.codeurjc.web.dto.PostMapper;
import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.dto.UserMapper;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.PostRepository;
import es.codeurjc.web.repository.UserRepository;

@Service
public class RankingService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    RankingService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public void calculatePostAverageRating(Long id) {

    }

    public Collection<UserDTO> topUsersApp() {
        return userMapper.toDTOs(userRepository.findTop5ByOrderByUserRateDesc());
    }

    public Collection<UserDTO> topUsersFollowed(UserDTO userDTO) {
        User user = userMapper.toDomain(userDTO);
        return userMapper.toDTOs(userRepository.findTopFollowedUsers(user.getId()));

    }

    public Collection<PostDTO> topPostsApp() {
        return postMapper.toDTOs(postRepository.findTop5ByOrderByAverageRatingDesc());

    }

    public Collection<PostDTO> topPostsFollowed(UserDTO userDTO) {
        User user = userMapper.toDomain(userDTO);
        return postMapper.toDTOs(postRepository.findTopPostsFollowedByUser(user.getId()));

    }

}
