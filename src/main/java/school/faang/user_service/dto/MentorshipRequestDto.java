package school.faang.user_service.dto;

import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

public record MentorshipRequestDto (
     Long id,
     String description,
     Long requesterId,
     Long receiverId,
     RequestStatus status,
     LocalDateTime createdAt,
     LocalDateTime updatedAt
) {}
