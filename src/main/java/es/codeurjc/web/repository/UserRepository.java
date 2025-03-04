package es.codeurjc.web.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import es.codeurjc.web.model.User;

@Component
public class UserRepository {

    private AtomicLong nextId = new AtomicLong(1L);
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

}
