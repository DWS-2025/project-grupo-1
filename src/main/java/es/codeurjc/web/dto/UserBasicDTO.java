package es.codeurjc.web.dto;

import java.util.List;

public record UserBasicDTO(
                Long id,
                String userName,
                String description,
                String email,
                String image,
                List<UserBasicDTO> followers,
                List<UserBasicDTO> followings,
                float averageRating) {
}
