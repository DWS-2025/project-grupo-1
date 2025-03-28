package es.codeurjc.web.dto;

import java.util.List;

import es.codeurjc.web.model.Post;
import es.codeurjc.web.model.User;

    public record CommentDTO(
        Long id,
        String content,
        User owner,
        Post commentedPost,
        String commentOwnerName,
        float  averageRating,
        int rating,
        List<PostDTO> posts) {
}
    

