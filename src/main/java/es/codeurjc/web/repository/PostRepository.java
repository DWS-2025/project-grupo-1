package es.codeurjc.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
 
import es.codeurjc.web.model.Post;


public interface PostRepository extends JpaRepository<Post, Long> {
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
