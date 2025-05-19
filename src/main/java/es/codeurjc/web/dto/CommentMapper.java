package es.codeurjc.web.dto;

import java.util.Collection;

import org.mapstruct.Mapper;

import es.codeurjc.web.model.Comment;

/**
 * Mapper interface for converting between Comment domain objects and their corresponding DTOs.
 * Utilizes MapStruct for automatic implementation generation.
 * <p>
 * Provides methods to map between:
 * <ul>
 *     <li>{@link Comment} and {@link CommentDTO}</li>
 *     <li>Collections of {@link Comment} and {@link CommentDTO}</li>
 *     <li>{@link CreateCommentDTO} and {@link Comment}</li>
 *     <li>{@link Comment} and {@link CommentBasicDTO}</li>
 * </ul>
 * <p>
 * This interface is intended to be used as a Spring component.
 * 
 * @author Grupo 1
 */
@Mapper(componentModel = "spring")

public interface CommentMapper {
    CommentDTO toDTO(Comment comment);

    Comment toDomain(CommentDTO commentDTO);

    Collection<CommentDTO> toDTOs(Collection<Comment> comments);

    Collection<Comment> toDomains(Collection<CommentDTO> commentDTOs);

    Comment toDomain(CreateCommentDTO commentDTO);

    CommentBasicDTO toCommentBasicDTO(Comment comment);

}
