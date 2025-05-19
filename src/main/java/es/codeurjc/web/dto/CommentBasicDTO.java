package es.codeurjc.web.dto;

import java.util.Objects;

/**
 * Data Transfer Object (DTO) representing the basic information of a comment.
 * <p>
 * This record encapsulates the essential fields of a comment, including its identifier,
 * content, owner, the post it comments on, and its rating. It provides custom implementations
 * of {@code equals} and {@code hashCode} based solely on the {@code id} field to ensure
 * correct behavior in collections and comparisons.
 * </p>
 *
 * @param id            the unique identifier of the comment
 * @param content       the textual content of the comment
 * @param owner         the basic information of the user who owns the comment
 * @param commentedPost the basic information of the post being commented on
 * @param rating        the rating associated with the comment
 * 
 * @author Grupo 1
 */
public record CommentBasicDTO(
        Long id,
        String content,
        UserBasicDTO owner,
        PostBasicDTO commentedPost,
        int rating) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommentBasicDTO that = (CommentBasicDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
