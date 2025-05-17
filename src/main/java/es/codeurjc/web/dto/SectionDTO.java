package es.codeurjc.web.dto;

import java.util.List;

public record SectionDTO(
        Long id,
        String title,
        String description,
        Float  averageRating,
        Integer numberOfPublications,
        List<PostBasicDTO> posts) {
}