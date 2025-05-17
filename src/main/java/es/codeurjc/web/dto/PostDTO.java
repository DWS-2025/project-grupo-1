package es.codeurjc.web.dto;

import java.util.List;

public record PostDTO(
        Long id,
        String title,
        String content,
        String image,
        UserBasicDTO owner,
        float  averageRating,
        List<CommentBasicDTO> comments,
        List<SectionBasicDTO> sections,
        List<UserBasicDTO> contributors) {
}
    

