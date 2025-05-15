package es.codeurjc.web.dto;

import java.sql.Blob;
import java.util.List;

public record SectionDTO(
        Long id,
        String title,
        String description,
        // Blob sectionImage,
        Float  averageRating,
        Integer numberOfPublications,
        List<PostBasicDTO> posts) {
}