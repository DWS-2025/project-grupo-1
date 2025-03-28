package es.codeurjc.web.dto;

import java.sql.Blob;
import java.util.List;

public record UserDTO(
        Long id,
        String userName,
        String description,
        String email,
        Blob userImage,
        List<PostDTO> posts,
        List<UserDTO> followers,
        List<UserDTO> followings,
        float  averageRating,
        List<SectionDTO> followedSections,
        List<CommentDTO> comments,
        List<PostDTO> contributedPosts) {
}
