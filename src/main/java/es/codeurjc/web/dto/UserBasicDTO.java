package es.codeurjc.web.dto;


public record UserBasicDTO(
        Long id,
        String userName,
        float  averageRating) {
}
