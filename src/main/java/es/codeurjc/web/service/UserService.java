package es.codeurjc.web.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Comment;
import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.Section;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    PostService postService;
   


    public User getLoggedUser(){
        return userRepository.getUserById(1);
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

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public User findByUserName(String userName){
        List<User> users = userRepository.findAll();
        User requestUser = null;
        for (User user : users) {
            if (user.getName().equals(userName)) {
                requestUser = user;
            }
        }
        return requestUser;
    }


    public Boolean isLogged(User user){
        return userRepository.findAll().get(0).equals(user);
    }

    public void deleteUser(User userToDelete){
        long id = userToDelete.getId();
        if(id!=1){
        List<Comment> comments = userToDelete.getComments();
        for (Comment comment : comments) {
            commentService.deleteCommentFromPost(comment.getCommentedPost(), comment.getId());
        }
        comments.clear();

        List<Post> posts = userToDelete.getPosts();
        for (Post post : posts) {
            postService.deletePost(post);
        }
        posts.clear();

        userToDelete.getCollaboratedPosts().clear();
        userRepository.deleteById(id);

        for(User user: userRepository.findAll())
        {
            if(user.getFollowers().contains(userToDelete))
            user.getFollowers().remove(userToDelete);

            if(user.getFollowings().contains(userToDelete))
            user.getFollowings().remove(userToDelete);

        }
        userToDelete.getFollowedSections().clear();
        userToDelete.getFollowers().clear();
        userToDelete.getFollowings().clear();
        
    }
}
}
