package es.codeurjc.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.PostRepository;
import es.codeurjc.web.repository.UserRepository;

@Service
public class RankingService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    RankingService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public void calculatePostAverageRating(Long id){
        
    }
    public List<User> topUsersApp() {
        return userRepository.findTop5ByOrderByUserRateDesc();
    }

    public List<User> topUsersFollowed(User user) {
        return userRepository.findTopFollowedUsers(user.getId());
        
    }

    public List<Post> topPostsApp() {
        return postRepository.findTop5ByOrderByAverageRatingDesc();
      
    }

    public List<Post> topPostsFollowed(User user) {
        return postRepository.findTopPostsFollowedByUser(user.getId());
      
    }

  
}
