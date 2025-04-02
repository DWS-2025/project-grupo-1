package es.codeurjc.web.dto;

import org.mapstruct.Mapper;

import es.codeurjc.web.model.Comment;

import java.util.Collection;
import java.util.List;


@Mapper(componentModel = "spring")
// This interface is used to map between Comment and CommentDTO objects
public interface CommentMapper {
    CommentDTO toDTO(Comment comment);

    Comment toDomain(CommentDTO commentDTO);
    Collection<CommentDTO> toDTOs(Collection<Comment> comments); 

    Collection<Comment> toDomains(Collection<CommentDTO> commentDTOs);
    
}
