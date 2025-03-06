package school.faang.user_service.dto.mentorship;

import lombok.Builder;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Builder
public record MentorshipResponseDto(
        Long id,
        String description,
        UserDto requester,
        UserDto receiver,
        RequestStatus status,
        String rejectionReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
