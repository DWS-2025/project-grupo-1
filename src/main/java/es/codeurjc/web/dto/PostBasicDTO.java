package es.codeurjc.web.dto;

public record PostBasicDTO(
        Long id,
        String title,
        UserBasicDTO owner,
        float averageRating
     ) {
}