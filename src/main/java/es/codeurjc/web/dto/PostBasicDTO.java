package es.codeurjc.web.dto;

public record PostBasicDTO(
        Long id,
        String title,
        String ownerName,
        float  averageRating
     ) {
}