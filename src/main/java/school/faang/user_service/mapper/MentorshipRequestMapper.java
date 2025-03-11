package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.events.MentorshipOfferedEvent;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest entity);

    MentorshipResponseDto toMentorshipResponseDto(MentorshipRequest entity);

    @Mapping(target = "sentMentorshipRequests", ignore = true)
    UserDto toUserDto(User user);

    User toUserEntity(UserDto userDto);

    @Mapping(target = "requesterId", source = "dto.requester.userId")
    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "mentorId", source = "dto.receiver.userId")
    MentorshipOfferedEvent toMentorshipOfferedEvent(MentorshipRequestDto dto,
                                                    MentorshipRequest request);
}
