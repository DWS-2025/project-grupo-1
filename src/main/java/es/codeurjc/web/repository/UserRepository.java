package es.codeurjc.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.web.model.User;



public interface UserRepository extends JpaRepository<User, Long>  {

    User findByUserName(String userName);
    
    List<User> findTop5ByOrderByUserRateDesc();

    @Query("SELECT u FROM UserTable u JOIN u.followers f WHERE f.id = :userId ORDER BY u.userRate DESC")
    List<User> findTopFollowedUsers(@Param("userId") Long userId);
    /* private AtomicLong nextId = new AtomicLong(1L);
    private ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();

    public List<User> findAll() {
        return users.values().stream().toList();
    }

    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findByName(String name) {
        return users.values().stream().filter(user -> user.getName().equals(name)).findFirst();
    }

    public void save(User user) {
        long id = user.getId();
        if (id == 0) {
            id = nextId.getAndIncrement();
            user.setId(id);
        }
        users.put(id, user);
    }

    public void deleteById(long id) {
        users.remove(id);
    }

    public User getUserById(long id) {
        return users.get(id);
    }
        */

}
