package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "username", source = "name")
    User toEntity(UserDto dto);

    @Mapping(target = "name", source = "username")
    UserDto toDto(User user);
}