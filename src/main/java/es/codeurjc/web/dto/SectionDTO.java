package es.codeurjc.web.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a section.
 *
 * @param id                   Unique identifier of the section.
 * @param title                Title of the section.
 * @param description          Description of the section.
 * @param image                URL or path to the section's image.
 * @param averageRating        Average rating of the section.
 * @param numberOfPublications Number of publications in the section.
 * @param posts                List of basic post DTOs associated with the section.
 * 
 * @author Grupo 1
 */
public record SectionDTO(
                Long id,
                String title,
                String description,
                String image,
                Float averageRating,
                Integer numberOfPublications,
                List<PostBasicDTO> posts) {
}