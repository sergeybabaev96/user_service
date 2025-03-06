package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalMapper {
    Goal toEntity(GoalDto goalDto);

    @Mapping(target = "parentId", source = "parent", qualifiedByName = "parentToParentId")
    @Mapping(target = "userIds", source = "users", qualifiedByName = "usersToUserIds")
    @Mapping(target = "skillIds", source = "skillsToAchieve", qualifiedByName = "skillsToSkillIds")
    GoalDto toDto(Goal goal);

    @Named("parentToParentId")
    default Long parentToParentId(Goal parent) {
        return parent.getId();
    }

    @Named("usersToUserIds")
    default List<Long> usersToUserIds(List<User> users) {
        return users.stream().map(User::getId).toList();
    }

    @Named("skillsToSkillIds")
    default List<Long> skillsToSkillIds(List<Skill> skillsToAchieve) {
        return skillsToAchieve.stream().map(Skill::getId).toList();
    }
}