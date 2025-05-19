package es.codeurjc.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.web.model.Post;

/**
 * Repository interface for managing {@link Post} entities.
 * <p>
 * Provides custom query methods for retrieving posts and calculating average ratings.
 * </p>
 *
 * <ul>
 *   <li>
 *     {@link #findAverageRatingByPostIdExcludingComment(Long, Long)}:
 *     Returns the average rating of a post, excluding a specific comment.
 *   </li>
 *   <li>
 *     {@link #findAverageRatingByPostId(Long)}:
 *     Returns the average rating of a post.
 *   </li>
 *   <li>
 *     {@link #findTop5ByOrderByAverageRatingDesc()}:
 *     Retrieves the top 5 posts ordered by their average rating in descending order.
 *   </li>
 *   <li>
 *     {@link #findTopPostsFollowedByUser(Long)}:
 *     Retrieves posts from users followed by a specific user, ordered by average rating.
 *   </li>
 * </ul>
 *
 * Extends {@link org.springframework.data.jpa.repository.JpaRepository} to provide standard CRUD operations.
 * 
 * @author Grupo 1
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    // Returns the average rating of a post
    @Query("SELECT COALESCE(AVG(c.rating), 0) FROM Comment c WHERE c.commentedPost.id = :postId AND c.id <> :commentId")
    float findAverageRatingByPostIdExcludingComment(@Param("postId") Long postId, @Param("commentId") Long commentId);

    @Query("SELECT COALESCE(AVG(c.rating), 0) FROM Comment c WHERE c.commentedPost.id = :postId")
    float findAverageRatingByPostId(@Param("postId") Long postId);

    List<Post> findTop5ByOrderByAverageRatingDesc();

    @Query("SELECT p FROM Post p JOIN p.owner u JOIN u.followers f WHERE f.id = :userId ORDER BY p.averageRating DESC")
    List<Post> findTopPostsFollowedByUser(@Param("userId") Long userId);

}
