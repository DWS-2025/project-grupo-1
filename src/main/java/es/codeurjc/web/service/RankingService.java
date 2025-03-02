package es.codeurjc.web.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.Model.Comment;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.Model.User;

@Service
public class RankingService {

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    public List<User> topUsersApp() {
        List<User> rankingUsers = new ArrayList<>(userService.findAllUsers());
        rankingUsers.sort(Comparator.comparing(User::getUserRate).reversed());
        return rankingUsers.subList(0, Math.min(rankingUsers.size(), 5));
    }

    public List<User> topUsersFollowed(User user) {
        List<User> rankingUsers = new ArrayList<>(user.getFollowing()); 
        rankingUsers.sort(Comparator.comparing(User::getUserRate).reversed());
        return rankingUsers.subList(0, Math.min(rankingUsers.size(), 5));
    }

    public List<Post> topPostsApp() {
        List<Post> rankingPost = new ArrayList<>(postService.findAllPosts());
        rankingPost.sort(Comparator.comparing(Post::getAverageRating).reversed());
        return rankingPost.subList(0, Math.min(rankingPost.size(), 5));
    }

    public List<Post> topPostsFollowed(User user) {
        List<Post> allFollowedPosts = new ArrayList<>();

      
        for (User followedUser : user.getFollowing()) {
            allFollowedPosts.addAll(followedUser.getPosts()); 
        }
      
        allFollowedPosts.sort(Comparator.comparing(Post::getAverageRating).reversed());

        return allFollowedPosts.subList(0, Math.min(allFollowedPosts.size(), 5));
    }

    public List<Comment> getCommentsRanking(Post post) {
        List<Comment> comments = new ArrayList<>(post.getComments()); 
        comments.sort(Comparator.comparing(Comment::getTotalLikes).reversed());
        return comments;
    }
}
