package es.codeurjc.web.dto;

import java.util.List;

public record CreatePostDTO(
    Long id,
    String title,
    String content,
    String image,
    List<SectionDTO> sectionDTO,
    List<UserDTO> userDTO
) {}