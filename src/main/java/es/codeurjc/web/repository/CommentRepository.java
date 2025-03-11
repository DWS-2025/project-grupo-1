package es.codeurjc.web.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import es.codeurjc.web.model.Comment;

@Component
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /* 
    private AtomicLong nextId = new AtomicLong(1L);
    private ConcurrentHashMap<Long, Comment> comments = new ConcurrentHashMap<>();

    public List<Comment> findAll(){
        return comments.values().stream().toList();
    }

    public Optional<Comment> findById(Long id){
        return Optional.ofNullable(comments.get(id));
    }

    public void save(Comment comment){
        long id = comment.getId();

        if (id == 0){
            id = nextId.getAndIncrement();
            comment.setId(id);
        }
        comments.put(id, comment);
    }

    public void deleteComment(Comment comment){
        comments.remove(comment.getId());
    } */
}




