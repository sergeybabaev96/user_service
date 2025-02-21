package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserEventDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(source = "preferenceNotification", target = "preference")
    UserEventDto toDtoForEvent(User user);

    List<UserDto> usersToUserDtos(List<User> users);
}
