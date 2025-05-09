package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipResponseDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", imports = {User.class, RequestStatus.class})
public interface MentorshipResponseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", expression = "java(new User(requestDto.getRequester()))")
    @Mapping(target = "receiver", expression = "java(new User(requestDto.getReceiver()))")
    @Mapping(target = "status", expression = "java(RequestStatus.PENDING)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    MentorshipRequest toRequestEntity(MentorshipRequestDto requestDto);

    @Mapping(source = "requester.id", target = "requester")
    @Mapping(source = "receiver.id", target = "receiver")
    @Mapping(source = "status", target = "status")
    MentorshipResponseDto toResponseDto(MentorshipRequest mentorshipRequest);
}
