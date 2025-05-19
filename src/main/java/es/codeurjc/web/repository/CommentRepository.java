package es.codeurjc.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Comment;

/**
 * Repository interface for managing {@link Comment} entities.
 * <p>
 * Extends {@link JpaRepository} to provide CRUD operations and pagination for comments.
 * </p>
 *
 * <p>
 * Custom query methods:
 * </p>
 * <ul>
 *   <li>
 *     {@code Page<Comment> findByCommentedPost(long postId, Pageable pageable)}:
 *     Retrieves a paginated list of comments associated with the specified post ID.
 *   </li>
 * </ul>
 * 
 * @author Grupo 1
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByCommentedPost(long postId, Pageable pageable);
}