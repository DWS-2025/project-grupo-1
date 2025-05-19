package es.codeurjc.web.dto;

import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) representing a user in the system.
 * <p>
 * This record encapsulates user-related data, including identification, profile information,
 * authentication details, associated posts, followers, followings, user rating, roles,
 * followed sections, comments, and collaborated posts.
 * </p>
 *
 * @param id                Unique identifier of the user.
 * @param userName          Username of the user.
 * @param description       Description or bio of the user.
 * @param password          User's password (should be handled securely).
 * @param cvFilePath        File path to the user's CV.
 * @param email             Email address of the user.
 * @param image             Path or URL to the user's profile image.
 * @param posts             List of posts created by the user.
 * @param followers         List of users who follow this user.
 * @param followings        List of users this user is following.
 * @param userRate          Rating of the user.
 * @param rols              List of roles assigned to the user.
 * @param followedSections  List of sections followed by the user.
 * @param comments          List of comments made by the user.
 * @param collaboratedPosts List of posts the user has collaborated on.
 * 
 * @author Grupo 1
 */
public record UserDTO(
        Long id,
        String userName,
        String description,
        String password,
        String cvFilePath,
        String email,
        String image,
        List<PostBasicDTO> posts,
        List<UserBasicDTO> followers,
        List<UserBasicDTO> followings,
        float userRate,
        List<String> rols,
        List<SectionBasicDTO> followedSections,
        List<CommentBasicDTO> comments,
        List<PostBasicDTO> collaboratedPosts) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
