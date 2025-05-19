package es.codeurjc.web.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.web.model.User;

/**
 * Mapper interface for converting between User domain objects and their corresponding DTOs.
 * Utilizes MapStruct for automatic implementation generation.
 * <p>
 * Provides methods to map between {@link User}, {@link UserDTO}, and {@link UserBasicDTO},
 * as well as collections of these types.
 * </p>
 *
 * <ul>
 *   <li>{@link #toDTO(User)}: Converts a User entity to a UserDTO.</li>
 *   <li>{@link #toBasicDTO(User)}: Converts a User entity to a UserBasicDTO.</li>
 *   <li>{@link #toDomain(UserDTO)}: Converts a UserDTO to a User entity.</li>
 *   <li>{@link #toDomain(UserBasicDTO)}: Converts a UserBasicDTO to a User entity.</li>
 *   <li>{@link #toDTOs(Collection)}: Converts a collection of User entities to UserDTOs.</li>
 *   <li>{@link #toDomains(Collection)}: Converts a collection of UserDTOs to User entities.</li>
 *   <li>{@link #toBasicDTOs(Collection)}: Converts a collection of User entities to UserBasicDTOs.</li>
 * </ul>
 *
 * <p>
 * The {@code @Mapper(componentModel = "spring")} annotation allows this mapper to be injected as a Spring bean.
 * </p>
 * 
 * @author Grupo 1
 */
@Mapper(componentModel = "spring")

public interface UserMapper {

    UserDTO toDTO(User user);

    UserBasicDTO toBasicDTO(User user);

    User toDomain(UserDTO userDTO);

    User toDomain(UserBasicDTO userBasicDTO);

    Collection<UserDTO> toDTOs(Collection<User> users);

    Collection<User> toDomains(Collection<UserDTO> userDTOs);

    List<UserBasicDTO> toBasicDTOs(Collection<User> users);

}
