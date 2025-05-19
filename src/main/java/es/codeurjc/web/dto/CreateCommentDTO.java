package es.codeurjc.web.dto;

/**
 * Data Transfer Object (DTO) for creating a new comment.
 *
 * @param content The textual content of the comment.
 * @param rating The rating associated with the comment.
 * 
 * @author Grupo 1
 */
public record CreateCommentDTO(
                String content,
                int rating) {
}
