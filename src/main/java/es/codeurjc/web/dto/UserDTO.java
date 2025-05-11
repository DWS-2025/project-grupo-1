package es.codeurjc.web.dto;

import java.util.List;

public record UserDTO(
        Long id,
        String userName,
        String description,
        String cvFilePath,
        String email,
        String image,
        List<PostBasicDTO> posts,
        List<UserBasicDTO> followers,
        List<UserBasicDTO> followings,
        float  averageRating,
        List<SectionBasicDTO> followedSections,
        List<CommentBasicDTO> comments,
        List<PostBasicDTO> collaboratedPosts) {
}
