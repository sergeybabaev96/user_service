package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    List<UserDto> usersToUserDtos(List<User> users);

    @Mapping(target = "preference", source = "contactPreference.preference")
    @Mapping(source = "skills", target = "skills")
    UserDto toDto(User user);

    default List<Long> mapSkillsToIds(List<Skill> skills) {
        if (skills == null) return null;
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}