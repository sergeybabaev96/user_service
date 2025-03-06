package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest entity);

    MentorshipResponseDto toMentorshipResponseDto(MentorshipRequest entity);

    MentorshipRequest toMentorshipRequestEntity(MentorshipRequestDto dto);

    @Mapping(target = "sentMentorshipRequests", ignore = true)
    UserDto toUserDto(User user);

    User toUserEntity(UserDto userDto);
}
