package es.codeurjc.web.dto;

import java.util.Collection;

import org.mapstruct.Mapper;

import es.codeurjc.web.model.Post;

/**
 * Mapper interface for converting between Post domain objects and their corresponding DTOs.
 * Utilizes MapStruct for automatic implementation generation.
 * <p>
 * This interface defines methods to map between:
 * <ul>
 *     <li>{@link Post} and {@link PostDTO}</li>
 *     <li>{@link Post} and {@link CreatePostDTO}</li>
 *     <li>Collections of {@link Post} and {@link PostDTO}</li>
 * </ul>
 * <p>
 * The {@code @Mapper(componentModel = "spring")} annotation allows this mapper to be injected as a Spring bean.
 * 
 * @author Grupo 1
 */
@Mapper(componentModel = "spring")

public interface PostMapper {
    PostDTO toDTO(Post post);

    PostDTO toDTO(CreatePostDTO postDTO);

    CreatePostDTO toCreatePostDTO(Post post);

    Post toDomain(PostDTO postDTO);

    Post toDomain(CreatePostDTO postDTO);

    Collection<PostDTO> toDTOs(Collection<Post> posts);

    Collection<Post> toDomains(Collection<PostDTO> postDTOs);

}
