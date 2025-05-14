package es.codeurjc.web.dto;

import java.util.List;

public record CreatePostDTO(
    String title,
    String content,
    String image,
    List<SectionDTO> sectionDTO,
    List<UserDTO> userDTO
) {}