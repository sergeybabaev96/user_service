package school.faang.user_service.dto;

import school.faang.user_service.entity.RequestStatus;

public record MentorshipFilterDto(String description, Long requester, Long receiver, RequestStatus status) {
}
