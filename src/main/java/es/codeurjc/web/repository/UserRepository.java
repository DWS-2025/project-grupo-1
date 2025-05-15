package es.codeurjc.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.web.model.User;



public interface UserRepository extends JpaRepository<User, Long>  {

    Optional<User> findByUserName(String userName);
    
    List<User> findTop5ByOrderByUserRateDesc();

    @Query("SELECT u FROM UserTable u JOIN u.followers f WHERE f.id = :userId ORDER BY u.userRate DESC")
    List<User> findTopFollowedUsers(@Param("userId") Long userId);


}
