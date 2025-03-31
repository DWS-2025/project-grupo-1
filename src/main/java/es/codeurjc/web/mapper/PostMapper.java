package es.codeurjc.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.web.dto.PostDTO;
import es.codeurjc.web.model.Post;

@Mapper(componentModel = "spring")
// This interface is used to map between Post and PostDTO objects
public interface PostMapper {
    PostDTO toDTO(Post post);

    Post toDomain(PostDTO postDTO); 

    List<PostDTO> toDTOs(List<Post> posts); 

    List<Post> toDomains(List<PostDTO> postDTOs); 
    
}
