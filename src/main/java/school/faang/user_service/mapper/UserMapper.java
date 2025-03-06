package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto userDto);

    @Mapping(target = "goalIds", source = "goals", qualifiedByName = "goalsToGoalIds")
    @Mapping(target = "skillIds", source = "skills", qualifiedByName = "skillsToSkillIds")
    UserDto toDto(User user);

    @Named("goalsToGoalIds")
    default List<Long> goalsToGoalIds(List<Goal> goals) {
        return goals.stream().map(Goal::getId).toList();
    }

    @Named("skillsToSkillIds")
    default List<Long> skillsToSkillIds(List<Skill> skills) {
        return skills.stream().map(Skill::getId).toList();
    }
}
