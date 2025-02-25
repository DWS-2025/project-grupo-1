package es.codeurjc.web.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import es.codeurjc.web.Model.Comment;

public class CommentRepository {
    private AtomicLong nextId = new AtomicLong(1L);
    private ConcurrentHashMap<Long, Comment> comments = new ConcurrentHashMap<>();

    public List<Comment> findAll(){
        return comments.values().stream().toList();
    }

    public Optional<Comment> findBy(Long id){
        return Optional.ofNullable(comments.get(id));
    }

    public void save (Comment comment){
        long id = comment.getId();

        if (id == 0){
            id = nextId.getAndIncrement();
            comment.setId(id);
        }
        comments.put(id, comment);
    }

    public void deleteById(long id){
        comments.remove(id);
    }
}




