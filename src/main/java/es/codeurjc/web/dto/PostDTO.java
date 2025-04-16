package es.codeurjc.web.dto;

import java.sql.Blob;
import java.util.List;

import es.codeurjc.web.model.User;

public record PostDTO(
        Long id,
        String title,
        String content,
        String image,
        User owner,
        String ownerName,
        float  averageRating,
        List<CommentBasicDTO> comments,
        List<SectionBasicDTO> sections,
        List<UserBasicDTO> contributors) {
}
    

