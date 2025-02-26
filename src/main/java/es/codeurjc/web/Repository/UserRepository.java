package es.codeurjc.web.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import es.codeurjc.web.Model.User;

@Component
public class UserRepository {
    
    private AtomicLong nextId = new AtomicLong(1L);
	private ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();

    public List<User> findAll() {
        return users.values().stream().toList();
    }

    public void save(User user) {
        long id = user.getId();
        if (id == 0) {
            id = nextId.getAndIncrement();
            user.setId(id);
        }
        users.put(id, user);
    }
    
}
