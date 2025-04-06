package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.User;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "mentorIds", expression = "java(UserMapper.toIds(user.getMentors()))")
    @Mapping(target = "menteeIds", expression = "java(UserMapper.toIds(user.getMentees()))")
    UserDto toDto(User user);

    @Mapping(target = "country", ignore = true)
    User toEntity(UserRegistrationDto dto);

    @Mapping(target = "avatarUrl", expression = "java(user.getUserProfilePic() != null ? user.getUserProfilePic().getFileId() : null)")
    UserResponseDto toResponseDto(User user);

    static List<Long> toIds(List<User> users) {
        return users == null ? List.of() : users.stream().map(User::getId).toList();
    }

    static List<UserDto> usersToUserDtos(List<User> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .toList();
    }
}