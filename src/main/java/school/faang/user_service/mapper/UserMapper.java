package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    List<SubscriptionUserDto> toDto(List<User> users);

    List<User> toEntity(List<SubscriptionUserDto> usersDto);

    List<UserDto> toUserDtoList(List<User> userList);

    List<User> toUserList(List<UserDto> userDtoList);

    User toEntity(UserRegistrationDto dto);

    UserRegistrationDto toRegistrationDto(User user);

}
