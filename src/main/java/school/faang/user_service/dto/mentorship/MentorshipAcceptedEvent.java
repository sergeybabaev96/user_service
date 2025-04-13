package school.faang.user_service.dto.mentorship;

import java.time.LocalDateTime;

public record MentorshipAcceptedEvent(
        Long id,
        Long requesterId,
        Long receiverId,
        LocalDateTime createdAt
) {


}
