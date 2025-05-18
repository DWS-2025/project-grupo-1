package es.codeurjc.web.dto;

import java.util.List;

public record SectionDTO(
        Long id,
        String title,
        String description,
        String image,
        Float  averageRating,
        Integer numberOfPublications,
        List<PostBasicDTO> posts) {
}