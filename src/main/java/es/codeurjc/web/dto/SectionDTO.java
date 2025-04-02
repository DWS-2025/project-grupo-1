package es.codeurjc.web.dto;

import java.sql.Blob;
import java.util.List;

public record SectionDTO(
        Long id,
        String title,
        String description,
        Blob sectionImage,
        float  averageRating,
        int numberOfPublications,
        List<PostBasicDTO> posts) {
}