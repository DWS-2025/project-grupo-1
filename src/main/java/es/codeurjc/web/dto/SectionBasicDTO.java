package es.codeurjc.web.dto;

public record SectionBasicDTO(
        Long id,
        String title,
        String description,
        float  averageRating,
        int numberOfPublications
        ) {
}
