package es.codeurjc.web.dto;

public record PostBasicDTO(
        Long id,
        String title,
        String ownerName,
        UserBasicDTO owner,
        float averageRating
     ) {
}