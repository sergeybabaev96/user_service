package school.faang.user_service.dto.mentorship;

import school.faang.user_service.entity.RequestStatus;

public record MentorshipFilterDto(String description, Long requester, Long receiver, RequestStatus status) {
}
