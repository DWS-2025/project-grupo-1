package es.codeurjc.web.dto;

import java.sql.Blob;
import java.util.List;

import es.codeurjc.web.model.User;

public record PostDTO(
        Long id,
        String title,
        String content,
        Blob postImage,
        User owner,
        String ownerName,
        float  averageRating,
        List<CommentDTO> comments,
        List<SectionDTO> sections,
        List<UserDTO> contributors) {
}
    

