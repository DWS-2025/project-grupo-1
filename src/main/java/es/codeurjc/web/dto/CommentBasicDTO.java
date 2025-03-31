package es.codeurjc.web.dto;


// maybe we dont need a basic DTO for comment, we can use the full DTO instead
public record CommentBasicDTO(
    Long id,
    String content,
    UserBasicDTO owner,
    PostBasicDTO commentedPost,
    String commentOwnerName,
    float  averageRating,
    int rating) {
}
