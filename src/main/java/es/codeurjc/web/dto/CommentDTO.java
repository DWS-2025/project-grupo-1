package es.codeurjc.web.dto;

/**
 * Data Transfer Object (DTO) representing a comment.
 *
 * @param id            Unique identifier of the comment.
 * @param content       Text content of the comment.
 * @param owner         Basic information about the user who made the comment.
 * @param commentedPost Basic information about the post being commented on.
 * @param rating        Rating or score associated with the comment.
 * 
 * @author Grupo 1
 */
public record CommentDTO(
        Long id,
        String content,
        UserBasicDTO owner,
        PostBasicDTO commentedPost,
        int rating) {
}
