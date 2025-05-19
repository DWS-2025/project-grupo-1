package es.codeurjc.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.web.model.User;

/**
 * Repository interface for managing {@link User} entities.
 * <p>
 * Extends {@link JpaRepository} to provide CRUD operations and custom queries for {@link User}.
 * </p>
 *
 * <ul>
 *   <li>{@code findByUserName(String userName)}: Retrieves a user by their username.</li>
 *   <li>{@code findTop5ByOrderByUserRateDesc()}: Retrieves the top 5 users ordered by their user rate in descending order.</li>
 *   <li>{@code findTopFollowedUsers(Long userId)}: Retrieves users followed by the specified user, ordered by user rate descending.</li>
 * </ul>
 * 
 * @author Grupo 1
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    List<User> findTop5ByOrderByUserRateDesc();

    @Query("SELECT u FROM UserTable u JOIN u.followers f WHERE f.id = :userId ORDER BY u.userRate DESC")
    List<User> findTopFollowedUsers(@Param("userId") Long userId);

}
