package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.user.RegisterUserDTO;
import co.com.crediya.api.dto.user.UserDTO;
import co.com.crediya.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    User toModelFromRegisterDTO(RegisterUserDTO registerUserDTO);
    UserDTO toUserDTOFromModel(User user);
}
