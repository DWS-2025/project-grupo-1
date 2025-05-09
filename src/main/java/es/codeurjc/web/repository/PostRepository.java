package es.codeurjc.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.web.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Returns the average rating of a post
    @Query("SELECT COALESCE(AVG(c.rating), 0) FROM Comment c WHERE c.commentedPost.id = :postId")
    float findAverageRatingByPostId(@Param("postId") Long postId);

    List<Post> findTop5ByOrderByAverageRatingDesc();

    @Query("SELECT p FROM Post p JOIN p.owner u JOIN u.followers f WHERE f.id = :userId ORDER BY p.averageRating DESC")
    List<Post> findTopPostsFollowedByUser(@Param("userId") Long userId);

    /* 
    private AtomicLong nextId = new AtomicLong(1L);
	private ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();

    public List<Post> findAll() {
        return posts.values().stream().toList();
    }

    public Optional<Post> findById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public void save(Post post) {
        long id = post.getId();
        if (id == 0) {
            id = nextId.getAndIncrement();
            post.setId(id);
        }
        posts.put(id, post);
    }

    public void deleteById(long id) {
        posts.remove(id);
    }
     */
}
