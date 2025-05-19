package es.codeurjc.web.dto;

/**
 * Data Transfer Object (DTO) representing basic user information.
 *
 * @param id         Unique identifier of the user.
 * @param userName   Username of the user.
 * @param description Short description or bio of the user.
 * @param email      Email address of the user.
 * @param image      URL or path to the user's profile image.
 * @param userRate   User's rating or score.
 * 
 * @author Grupo 1
 */
public record UserBasicDTO(
        Long id,
        String userName,
        String description,
        String email,
        String image,
        float userRate) {
}
