package school.faang.user_service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;

@Slf4j
public abstract class MentorshipRequestMapperDecorator extends MentorshipRequestMapper {
    @Autowired
    private MentorshipRequestMapper delegate;

    @Override
    public MentorshipRequest toEntity(MentorshipRequestDto dto) {
        MentorshipRequest request = delegate.toEntity(dto);

        if (request.getRequester().getId().equals(request.getReceiver().getId())) {
            throw new IllegalArgumentException("Requester and receiver cannot be the same user");
        }

        log.info("Mentorship request created: " + request);
        return request;
    }

    @Override
    public MentorshipRequest updateRequestFromDto(RejectionDto rejectionDto, MentorshipRequest mentorshipRequest) {
        MentorshipRequest updatedRequest = delegate.updateRequestFromDto(rejectionDto, mentorshipRequest);
        log.info("Mentorship request updated to REJECTED: " + updatedRequest);
        return updatedRequest;
    }

    @Override
    public MentorshipRequestDto toDto(MentorshipRequest request) {
        MentorshipRequestDto dto = delegate.toDto(request);
        return dto;
    }
}