package es.codeurjc.web.dto;

public record SectionBasicDTO(
        Long id,
        String title,
        String description,
        Float  averageRating,
        Integer numberOfPublications) {
}
