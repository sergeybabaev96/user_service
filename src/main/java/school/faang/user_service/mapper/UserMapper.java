package school.faang.user_service.mapper;

import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public interface UserMapper {
    User toEntity(UserDto userDto);

    UserDto toDto(User user);
}
