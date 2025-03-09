package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "mentors", target = "mentorIds", expression ="java(toIds(user.getMentors))")
    @Mapping(source = "mentees", target = "menteeIds", expression = "java(toIds(user.getMentees))")
    UserDto toDto(User user);

    static List<Long> toIds(List<User> users){
        return users == null ? List.of() : users.stream().map(User::getId).toList();
    }

    static List<UserDto> usersToUserDtos(List<User> users) {
        return users.stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .toList();
    }
}