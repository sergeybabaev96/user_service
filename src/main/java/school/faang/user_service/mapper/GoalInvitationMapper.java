package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "spring")
public interface GoalInvitationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "inviterId", target = "inviter")
    @Mapping(source = "invitedUserId", target = "invited")
    @Mapping(source = "goalId", target = "goal")
    GoalInvitation toEntity(GoalInvitationDto dto);

    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    @Mapping(source = "goal.id", target = "goalId")
    GoalInvitationDto toDto(GoalInvitation entity);

    default User mapToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return User.builder().id(userId).build();
    }

    default Goal mapToGoal(Long goalId) {
        if (goalId == null) {
            return null;
        }
        return Goal.builder().id(goalId).build();
    }
}