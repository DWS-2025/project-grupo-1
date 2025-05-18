package es.codeurjc.web.dto;

public record CreateCommentDTO(
        String content,
        int rating) {
}
