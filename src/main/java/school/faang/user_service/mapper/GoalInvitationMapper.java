package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDtoResponse;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    @Mapping(source = "goal.id", target = "goalId")
    GoalInvitationDtoResponse toGoalInvitationDtoResponse(GoalInvitation invitation);

    @Mapping(target = "inviter", ignore = true)
    @Mapping(target = "invited", ignore = true)
    @Mapping(target = "goal", ignore = true)
    GoalInvitation toGoalInvitationEntity(GoalInvitationDto dto);
}
