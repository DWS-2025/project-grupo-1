package es.codeurjc.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.model.User;

@Mapper(componentModel = "spring")

// This interface is used to map between User and UserDTO objects
public interface UserMapper {

    UserDTO toDTO(User user);

    User toDomain(UserDTO userDTO);

    List<UserDTO> toDTOs(List<User> users);

    List<User> toDomains(List<UserDTO> userDTOs);
    
}
