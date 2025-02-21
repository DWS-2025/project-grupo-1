package es.codeurjc.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class RankingManager {

    private List<User> users;
    private List<Post> posts;
    private Manager manager;

    public RankingManager(){
        manager = new Manager();
        this.users = manager.getAplicationUsers();
        this.posts = manager.getAplicationPosts();
    }


    public List<User> topUsers() {
        List<User> rankingUsers = users;
        rankingUsers.sort(Comparator.comparing(User::getRate).reversed()); //ordena de manera descendente a los usuarios en funcion de su evaluación

        return rankingUsers.subList(0, Math.min(rankingUsers.size(), 10)); //return 10 most valued users
    }


    public List<Post> topPosts(){
        List<Post> rankingPost = posts;
        rankingPost.sort(Comparator.comparing(Post::getAverageRating).reversed());

        return rankingPost.subList(0, Math.min(rankingPost.size(), 10));
    }


    public List<Comment> getCommentsRanking (Post post){
        List<Comment> comments = post.getComments();
        comments.sort(Comparator.comparing(Comment::getTotalLikes).reversed());

        return comments;
    }


    public float postsAverageRating(Post post) { 
        List<Comment> comments = post.getComments();
        float rate = 0;

        for (Comment comment: comments) {
            rate += comment.getRate();
        }

        return rate /= comments.size(); 

    }

    
}





