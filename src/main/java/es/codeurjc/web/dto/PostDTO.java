package es.codeurjc.web.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a post.
 *
 * @param id              Unique identifier of the post.
 * @param title           Title of the post.
 * @param content         Main content or body of the post.
 * @param image           URL or path to the image associated with the post.
 * @param owner           Basic information about the user who owns the post.
 * @param averageRating   Average rating of the post.
 * @param comments        List of basic information about comments on the post.
 * @param sections        List of basic information about sections within the post.
 * @param contributors    List of users who contributed to the post.
 * 
 * @author Grupo 1
 */
public record PostDTO(
                Long id,
                String title,
                String content,
                String image,
                UserBasicDTO owner,
                float averageRating,
                List<CommentBasicDTO> comments,
                List<SectionBasicDTO> sections,
                List<UserBasicDTO> contributors) {
}
