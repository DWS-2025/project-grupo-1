package es.codeurjc.web;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.web.Model.Comment;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.Model.User;
import es.codeurjc.web.service.Manager;
import es.codeurjc.web.service.UserService;
import es.codeurjc.web.service.PostService;

@Component
public class RankingManager {
    @Autowired
    private UserService userRepository;
    @Autowired
    private PostService postRepository;

    private List<User> users;
    private List<Post> posts;

    public RankingManager(){
        this.users = userRepository.findAllUsers();
        this.posts = postRepository.findAllPosts();
    }


    public List<User> topUsersApp() {
        List<User> rankingUsers = users;
        rankingUsers.sort(Comparator.comparing(User::getUserRate).reversed()); //ordena de manera descendente a los usuarios en funcion de su evaluación
        return rankingUsers.subList(0, Math.min(rankingUsers.size(), 10)); //return 10 most valued users
    }

    public List<User> topUsersFollowed(User user) {
        List<User> rankingUsers = user.getFollowers();
        rankingUsers.sort(Comparator.comparing(User::getUserRate).reversed()); //ordena de manera descendente a los usuarios en funcion de su evaluación
        return rankingUsers.subList(0, Math.min(rankingUsers.size(), 10)); //return 10 most valued users
    }


    public List<Post> topPostsApp(){
        List<Post> rankingPost = posts;
        rankingPost.sort(Comparator.comparing(Post::getAverageRating).reversed());
        return rankingPost.subList(0, Math.min(rankingPost.size(), 10));
    }
    public List<Post> topPostsFollowed( User user){
        List<Post> rankingPost = user.getPosts();
        rankingPost.sort(Comparator.comparing(Post::getAverageRating).reversed());
        return rankingPost.subList(0, Math.min(rankingPost.size(), 10));
    }


    public List<Comment> getCommentsRanking (Post post){
        List<Comment> comments = post.getComments();
        comments.sort(Comparator.comparing(Comment::getTotalLikes).reversed());
        return comments;
    }
}


