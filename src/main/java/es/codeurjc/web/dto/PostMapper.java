package es.codeurjc.web.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.web.model.Post;

@Mapper(componentModel = "spring")
// This interface is used to map between Post and PostDTO objects
public interface PostMapper {
    PostDTO toDTO(Post post);

    PostDTO toDTO(CreatePostDTO postDTO);
    

    CreatePostDTO toCreatePostDTO(Post post);

    Post toDomain(PostDTO postDTO);

    Post toDomain(CreatePostDTO postDTO);

    Collection<PostDTO> toDTOs(Collection<Post> posts); 

    Collection<Post> toDomains(Collection<PostDTO> postDTOs); 
    
}
