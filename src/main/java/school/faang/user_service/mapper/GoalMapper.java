package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalViewDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalMapper {
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toEntity(GoalCreateDto goal);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "users", target = "usersId", qualifiedByName = "usersToIds")
    @Mapping(source = "skillsToAchieve", target = "skillsToAchieveId", qualifiedByName = "skillsToIds")
    GoalViewDto toDto(Goal goal);

    void update(GoalCreateDto goalDto, @MappingTarget Goal goal);

    @Named("skillsToIds")
    default List<Long> skillsToIds(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return null;
        }
        return skills.stream().map(Skill::getId).toList();
    }

    @Named("usersToIds")
    default List<Long> usersToIds(List<User> users) {
        if (users == null || users.isEmpty()) {
            return null;
        }
        return users.stream().map(User::getId).toList();
    }
}
