package es.codeurjc.web.dto;

/**
 * Data Transfer Object (DTO) for creating a new section.
 * <p>
 * Encapsulates the information required to create a section, including its title and description.
 *
 * @param title       the title of the section
 * @param description the description of the section
 * 
 * @author Grupo 1
 */
public record CreateSectionDTO(
                String title,
                String description) {
}