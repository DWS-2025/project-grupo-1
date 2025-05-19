package es.codeurjc.web.dto;

/**
 * Data Transfer Object (DTO) representing basic information about a post.
 *
 * @param id             the unique identifier of the post
 * @param title          the title of the post
 * @param owner          the basic information of the user who owns the post
 * @param averageRating  the average rating of the post
 * 
 * @author Grupo 1
 */
public record PostBasicDTO(
                Long id,
                String title,
                UserBasicDTO owner,
                float averageRating) {
}