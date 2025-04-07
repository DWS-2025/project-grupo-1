package es.codeurjc.web.dto;

public record UserBasicDTO(
                Long id,
                String userName,
                String description,
                String email,
                String image,
                float averageRating) {
}
