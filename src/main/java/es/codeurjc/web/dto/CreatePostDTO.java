package es.codeurjc.web.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) for creating a new post.
 * <p>
 * This record encapsulates the necessary information required to create a post,
 * including its title, content, associated image, sections, and users.
 * </p>
 *
 * @param title      the title of the post
 * @param content    the main content/body of the post
 * @param image      the URL or path to the image associated with the post
 * @param sectionDTO the list of sections related to the post
 * @param userDTO    the list of users associated with the post
 * 
 * @author Grupo 1
 */
public record CreatePostDTO(
        String title,
        String content,
        String image,
        List<SectionDTO> sectionDTO,
        List<UserDTO> userDTO) {
}