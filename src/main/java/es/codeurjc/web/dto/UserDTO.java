package es.codeurjc.web.dto;

import java.util.List;
import java.util.Objects;

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
