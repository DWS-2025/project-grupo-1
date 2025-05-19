package es.codeurjc.web.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.web.model.User;

@Mapper(componentModel = "spring")

// This interface is used to map between User and UserDTO objects
public interface UserMapper {

    UserDTO toDTO(User user);

    UserBasicDTO toBasicDTO(User user);

    User toDomain(UserDTO userDTO);

    User toDomain(UserBasicDTO userBasicDTO);

    Collection<UserDTO> toDTOs(Collection<User> users);

    Collection<User> toDomains(Collection<UserDTO> userDTOs);

    List<UserBasicDTO> toBasicDTOs(Collection<User> users);

}
