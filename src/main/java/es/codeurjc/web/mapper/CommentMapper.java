package es.codeurjc.web.mapper;

import org.mapstruct.Mapper;

import es.codeurjc.web.dto.CommentDTO;
import es.codeurjc.web.model.Comment;
import java.util.List;


@Mapper(componentModel = "spring")
// This interface is used to map between Comment and CommentDTO objects
public interface CommentMapper {
    CommentDTO toDTO(Comment comment);

    Comment toDomain(CommentDTO commentDTO);
    List<CommentDTO> toDTOs(List<Comment> comments); 

    List<Comment> toDomains(List<CommentDTO> commentDTOs);
    
}
