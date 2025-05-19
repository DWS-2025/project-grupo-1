package es.codeurjc.web.dto;

/**
 * Data Transfer Object (DTO) representing basic information about a section.
 *
 * @param id                   the unique identifier of the section
 * @param title                the title of the section
 * @param description          a brief description of the section
 * @param averageRating        the average rating of the section
 * @param numberOfPublications the number of publications in the section
 * 
 * @author Grupo 1
 */
public record SectionBasicDTO(
                Long id,
                String title,
                String description,
                Float averageRating,
                Integer numberOfPublications) {
}
