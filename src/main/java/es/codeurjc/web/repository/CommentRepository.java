package es.codeurjc.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByCommentedPost(long postId, Pageable pageable);
}