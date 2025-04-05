package es.codeurjc.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Comment;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    
    Comment findByOwner(String userName);
}




