package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalCreateRequestDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.GoalUpdateRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    Goal toGoalEntity(final GoalCreateRequestDto goalCreateRequestDto);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(target = "completed", expression = "java(mapGoalStatus(goal.getStatus()))")
    @Mapping(source = "createdAt", target = "createdDate")
    @Mapping(source = "updatedAt", target = "updatedDate")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(target = "invitationsIds", expression = "java(mapInvitationIds(goal.getInvitations()))")
    @Mapping(target = "usersIds", expression = "java(mapUserIds(goal.getUsers()))")
    @Mapping(target = "skillToAchieveIds", expression = "java(mapSkillIds(goal.getSkillsToAchieve()))")
    GoalResponseDto toGoalResponseDto(final Goal goal);

    @Mapping(target = "status", expression = "java(mapGoalStatus(goalUpdateRequestDto.getCompleted()))")
    void update(@MappingTarget Goal goal, final GoalUpdateRequestDto goalUpdateRequestDto);

    default boolean mapGoalStatus(GoalStatus status) {
        return Objects.equals(status, GoalStatus.COMPLETED);
    }

    default GoalStatus mapGoalStatus(boolean completed) {
        return completed ? GoalStatus.COMPLETED : GoalStatus.ACTIVE;
    }

    default List<Long> mapInvitationIds(List<GoalInvitation> invitations) {
        if (invitations == null) {
            return new ArrayList<>();
        }
        return invitations.stream()
                .map(GoalInvitation::getId)
                .toList();
    }

    default List<Long> mapUserIds(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream()
                .map(User::getId)
                .toList();
    }

    default List<Long> mapSkillIds(List<Skill> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}
