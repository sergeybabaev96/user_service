package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill toEntity(SkillDto skillDto);

    @Mapping(target = "userIds", source = "users", qualifiedByName = "usersToUserIds")
    @Mapping(target = "goalIds", source = "goals", qualifiedByName = "goalsToGoalIds")
    SkillDto toDto(Skill skill);

    @Named("usersToUserIds")
    default List<Long> usersToUserIds(List<User> users) {
        return users.stream().map(User::getId).toList();
    }

    @Named("goalsToGoalIds")
    default List<Long> goalsToGoalIds(List<Goal> goals) {
        return goals.stream().map(Goal::getId).toList();
    }
}
