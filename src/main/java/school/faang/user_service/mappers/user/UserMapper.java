package school.faang.user_service.mappers.user;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToUserDTO(User user);

    User userDTOToUser(UserDto dto);
}
