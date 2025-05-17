package es.codeurjc.web.dto;

import java.util.Objects;

// maybe we dont need a basic DTO for comment, we can use the full DTO instead
public record CommentBasicDTO(
        Long id,
        String content,
        UserBasicDTO owner,
        PostBasicDTO commentedPost,
        String commentOwnerName,
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
