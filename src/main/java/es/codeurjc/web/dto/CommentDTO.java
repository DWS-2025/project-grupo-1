package es.codeurjc.web.dto;

    public record CommentDTO(
        Long id,
        String content,
        UserBasicDTO owner,
        PostBasicDTO commentedPost,
        String commentOwnerName,
        int rating) {
}
    

