package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(source = "receiver.id", target = "receiverId", ignore = true)
    @Mapping(source = "requester.id", target = "requesterId", ignore = true)
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(source = "receiverId", target = "receiver.id", ignore = true)
    @Mapping(source = "requesterId", target = "requester.id", ignore = true)
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);
}
