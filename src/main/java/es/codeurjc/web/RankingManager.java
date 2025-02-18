package es.codeurjc.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankingManager {

    private List<User> users;
    private List<Post> posts;

    public RankingManager(){
        this.users = new ArrayList<>();
        this.posts = new ArrayList<>();
    }


    public List<User> getUsersRanking() {
        List<User> rankingUsers = users;
        rankingUsers.sort(Comparator.comparing(User::getRate).reversed()); 

        return rankingUsers.subList(0, Math.min(rankingUsers.size(), 10)); //return 10 most valued users
    }


    public List<Post> getPostsRanking(){
        List<Post> rankingPost = posts;
        rankingPost.sort(Comparator.comparing(Post::getAverageRating).reversed());

        return rankingPost.subList(0, Math.min(rankingPost.size(), 10));
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
